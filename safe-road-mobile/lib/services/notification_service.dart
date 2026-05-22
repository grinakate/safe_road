import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart'
    as sse_consts;
import 'package:flutter_client_sse/flutter_client_sse.dart' as sse;

import '../core/constants.dart';
import '../theme/app_colors.dart';
import '../theme/app_text_styles.dart';
import 'api_client.dart';

class NotificationEvent {
  final String title;
  final String content;

  NotificationEvent({required this.title, required this.content});

  factory NotificationEvent.fromEvent(String eventName, String data) {
    return NotificationEvent(title: eventName, content: data);
  }
}

class NotificationService {
  final ApiV1Client _apiClient;
  final GlobalKey<NavigatorState> _navigatorKey;

  final List<NotificationEvent> _queue = [];
  bool _busy = false;

  StreamSubscription<sse.SSEModel>? _subscription;
  String? _lastEventId;
  int _reconnectAttempts = 0;
  Timer? _watchdogTimer;

  NotificationService(this._apiClient, this._navigatorKey);

  /// Запустить подписку на SSE (вызвать после успешного логина и сохранения токена)
  void start() {
    _startListening();
  }

  void setBusy(bool busy) {
    _busy = busy;
    if (!_busy) {
      _flushQueue();
    }
  }

  bool get isBusy => _busy;

  void _startListening() async {
    final uri = Uri.parse(
      '${AppConstants.baseUrl}${ApiV1Client.apiVersion}/notifications/subscribe',
    );
      try {
        final headers = await _apiClient.getAuthHeaders();
        // Ensure correct headers for SSE subscription
        headers['Accept'] = 'text/event-stream';
        headers['Cache-Control'] = 'no-cache';
        headers['Connection'] = 'keep-alive';
        // Если есть id последнего полученного события — просим сервер возобновить поток с этого места
        if (_lastEventId != null && _lastEventId!.isNotEmpty) {
          headers['Last-Event-ID'] = _lastEventId!;
        }

      // Используем flutter_client_sse: получаем Stream<SSEModel>
      try {
        final stream = sse.SSEClient.subscribeToSSE(
          method: sse_consts.SSERequestType.GET,
          url: uri.toString(),
          header: headers,
        );

        _subscription = stream.listen(
          (sse.SSEModel model) {
            // сохраняем id последнего события (если предоставлено), чтобы при переподключении
            // можно было попросить сервер прислать события с нужного места
            try {
              final idVal = (model.id ?? '').toString();
              if (idVal.isNotEmpty) _lastEventId = idVal;
            } catch (_) {}

            final String eventName = (model.event ?? '').trim();
            final String data = (model.data ?? '').trim();
            if (data.isNotEmpty) {
              final evt = NotificationEvent.fromEvent(
                eventName.isEmpty ? 'notification' : eventName,
                data,
              );
              // Успешный приход события — сбрасываем счётчик попыток переподключения
              _reconnectAttempts = 0;
              _resetWatchdog();
              _onEvent(evt);
            }
          },
          onDone: () {
            _stopWatchdog();
            _scheduleReconnect();
          },
          onError: (err) {
            _stopWatchdog();
            _scheduleReconnect();
          },
          cancelOnError: true,
        );
        _startWatchdog();
      } catch (e) {
        // если подписка не удалась — переподключаемся позже
        _scheduleReconnect();
      }
    } catch (e) {
      // reconnect
      _scheduleReconnect();
    }
  }

  void _scheduleReconnect() {
    _reconnectAttempts = (_reconnectAttempts + 1).clamp(0, 10);
    // экспоненциальная задержка: 2,4,8,... ограничим 60 сек
    final delaySeconds = (2 << (_reconnectAttempts - 1)).clamp(2, 60);
    Future.delayed(Duration(seconds: delaySeconds), () {
      _startListening();
    });
  }

  void _cancelSubscription() {
    try {
      _subscription?.cancel();
    } catch (_) {}
    try {
      sse.SSEClient.unsubscribeFromSSE();
    } catch (_) {}
    _subscription = null;
    _stopWatchdog();
  }

  void _startWatchdog() {
    _watchdogTimer?.cancel();
    // Если в течение 90 секунд не пришло ни одного события — считаем соединение мёртвым и переподключаемся
    _watchdogTimer = Timer(const Duration(seconds: 90), () {
      _cancelSubscription();
      _scheduleReconnect();
    });
  }

  void _resetWatchdog() {
    if (_watchdogTimer != null && _watchdogTimer!.isActive) {
      _watchdogTimer!.cancel();
      _startWatchdog();
    }
  }

  void _stopWatchdog() {
    try {
      _watchdogTimer?.cancel();
    } catch (_) {}
    _watchdogTimer = null;
  }

  // Используем парсинг, предоставляемый flutter_client_sse; ручной буфер и
  // парсер по частям больше не нужны.

  void _onEvent(NotificationEvent evt) {
    if (_busy) {
      _queue.add(evt);
      return;
    }
    if (evt.title == 'REWARDS_EARNED') {
      _showRewardDialog(evt);
    } else {
      _showDialogFor(evt);
    }
  }

  void _flushQueue() {
    if (_queue.isEmpty) return;
    Future.microtask(() async {
      while (_queue.isNotEmpty) {
        final evt = _queue.removeAt(0);
        if (evt.title == 'REWARDS_EARNED') {
          await _showRewardDialog(evt);
        } else {
          await _showDialogFor(evt);
        }
      }
    });
  }

  Future<void> _showDialogFor(NotificationEvent evt) async {
    final context = _navigatorKey.currentState?.context;
    if (context == null) return;

    return showDialog<void>(
      context: context,
      barrierDismissible: true,
      builder: (context) {
        return AlertDialog(
          title: Text(evt.title.isNotEmpty ? evt.title : 'Уведомление'),
          content: Text(evt.content),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Ок'),
            ),
          ],
        );
      },
    );
  }

  Future<void> _showRewardDialog(NotificationEvent evt) async {
    final context = _navigatorKey.currentState?.context;
    if (context == null) return;

    // ожидаем, что evt.content — JSON с полями earnedXp, totalXp, levelUp, newLevel
    Map<String, dynamic> payload = {};
    try {
      payload = jsonDecode(evt.content) as Map<String, dynamic>;
    } catch (_) {}

    final int earnedXp = (payload['earnedXp'] ?? 0) as int;
    final int totalXp = (payload['totalXp'] ?? 0) as int;
    final bool levelUp = (payload['levelUp'] ?? false) as bool;
    final int newLevel = (payload['newLevel'] ?? 0) as int;

    return showDialog<void>(
      context: context,
      barrierDismissible: true,
      builder: (context) {
        return Dialog(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          child: Container(
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Image.asset(
                  'assets/images/congratulations.png',
                  width: 96,
                  height: 96,
                  errorBuilder: (c, e, s) => Icon(
                    Icons.card_giftcard,
                    size: 64,
                    color: AppColors.orangeCatAccent,
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  levelUp ? 'Новый уровень!' : 'Поздравляем!',
                  style: AppTextStyles.achievementTitle,
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),
                if (levelUp)
                  Text(
                    'Вы достигли уровня $newLevel',
                    style: AppTextStyles.bodyLarge,
                  ),
                const SizedBox(height: 8),
                Text('Получено очков опыта', style: AppTextStyles.bodyMedium),
                const SizedBox(height: 4),
                Text('+$earnedXp', style: AppTextStyles.achievementXp),
                const SizedBox(height: 8),
                Text('Всего: $totalXp XP', style: AppTextStyles.body14),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.primaryGreen,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                    ),
                    onPressed: () => Navigator.of(context).pop(),
                    child: Text('Отлично', style: AppTextStyles.buttonText),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  void dispose() {
    try {
      _subscription?.cancel();
    } catch (_) {}

    // Прекращаем подписку и уведомляем библиотеку закрыть соединение
    try {
      sse.SSEClient.unsubscribeFromSSE();
    } catch (_) {}

    _subscription = null;
  }
}
