import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_client_sse/flutter_client_sse.dart' as sse;
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart' as sse_consts;
import 'package:provider/provider.dart';
import '../core/service_locator.dart';
import '../services/api_client.dart';
import '../core/constants.dart';
import '../theme/app_text_styles.dart';
import '../theme/app_colors.dart';
import 'game_profile_provider.dart';

class NotificationEvent {
  final String title;
  final String content;

  NotificationEvent({required this.title, required this.content});

  factory NotificationEvent.fromEvent(String eventName, String data) {
    return NotificationEvent(title: eventName, content: data);
  }
}

class NotificationProvider extends ChangeNotifier {
  final ApiV1Client _apiClient = getIt<ApiV1Client>();
  final GlobalKey<NavigatorState> _navigatorKey = getIt<GlobalKey<NavigatorState>>();

  final List<NotificationEvent> _queue = [];
  bool _isUserBusy = false;

  StreamSubscription<sse.SSEModel>? _subscription;

  bool get isUserBusy => _isUserBusy;

  void setUserBusy(bool busy) {
    _isUserBusy = busy;
    if (!_isUserBusy) {
      _flushQueue();
    }
    notifyListeners();
  }

  void addNotification(NotificationEvent evt) {
    if (_isUserBusy) {
      _queue.add(evt);
      return;
    }
    _handleEvent(evt);
  }

  void _handleEvent(NotificationEvent evt) {
    if (evt.title == 'REWARDS_EARNED') {
      _showRewardDialog(evt);
    } else {
      _showDialogFor(evt);
    }
  }

  Future<void> _flushQueue() async {
    if (_queue.isEmpty) return;
    while (_queue.isNotEmpty) {
      final evt = _queue.removeAt(0);
      await Future.microtask(() => _handleEvent(evt));
    }
  }

  void start() async {
    _startListening();
  }

  void _startListening() async {
    final uri = Uri.parse('${AppConstants.baseUrl}${ApiV1Client.apiVersion}/notifications/subscribe');
    try {
      final headers = await _apiClient.getAuthHeaders();
      final stream = sse.SSEClient.subscribeToSSE(
        method: sse_consts.SSERequestType.GET,
        url: uri.toString(),
        header: headers,
      );
      _subscription = stream.listen((sse.SSEModel model) {
        final String eventName = (model.event ?? '').trim();
        final String data = (model.data ?? '').trim();
        if (data.isNotEmpty) {
          final evt = NotificationEvent.fromEvent(
            eventName.isEmpty ? 'notification' : eventName,
            data,
          );
          addNotification(evt);
          if (evt.title == 'REWARDS_EARNED') {
            // также обновляем профиль
            try {
              final Map<String, dynamic> payload = jsonDecode(evt.content) as Map<String, dynamic>;
              final reward = RewardNotification.fromJson(payload);
              // обновляем через провайдер GameProfileProvider, если он доступен
              // get via global navigator context
              final context = _navigatorKey.currentState?.context;
              if (context != null) {
                final gp = context.read<GameProfileProvider>();
                gp.updateFromNotification(reward);
              }
            } catch (_) {}
          }
        }
      }, onDone: () {
        Future.delayed(const Duration(seconds: 2), _startListening);
      }, onError: (err) {
        Future.delayed(const Duration(seconds: 2), _startListening);
      }, cancelOnError: true);
    } catch (e) {
      Future.delayed(const Duration(seconds: 2), _startListening);
    }
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
            TextButton(onPressed: () => Navigator.of(context).pop(), child: const Text('Ок')),
          ],
        );
      },
    );
  }

  Future<void> _showRewardDialog(NotificationEvent evt) async {
    final context = _navigatorKey.currentState?.context;
    if (context == null) return;
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
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          child: Container(
            padding: const EdgeInsets.all(16),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Image.asset('assets/images/congratulations.png', width: 96, height: 96, errorBuilder: (c, e, s) => Icon(Icons.card_giftcard, size: 64, color: AppColors.orangeCatAccent)),
                const SizedBox(height: 12),
                Text(levelUp ? 'Новый уровень!' : 'Поздравляем!', style: AppTextStyles.achievementTitle, textAlign: TextAlign.center),
                const SizedBox(height: 8),
                if (levelUp) Text('Вы достигли уровня $newLevel', style: AppTextStyles.bodyLarge),
                const SizedBox(height: 8),
                Text('Получено очков опыта', style: AppTextStyles.bodyMedium),
                const SizedBox(height: 4),
                Text('+$earnedXp', style: AppTextStyles.achievementXp),
                const SizedBox(height: 8),
                Text('Всего: $totalXp XP', style: AppTextStyles.body14),
                const SizedBox(height: 16),
                SizedBox(width: double.infinity, child: ElevatedButton(style: ElevatedButton.styleFrom(backgroundColor: AppColors.primaryGreen, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8))), onPressed: () => Navigator.of(context).pop(), child: Text('Отлично', style: AppTextStyles.buttonText))),
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
    try {
      sse.SSEClient.unsubscribeFromSSE();
    } catch (_) {}
    _subscription = null;
    super.dispose();
  }

  /// Остановить подписку (не уничтожая провайдер)
  void stop() {
    try {
      _subscription?.cancel();
    } catch (_) {}
    try {
      sse.SSEClient.unsubscribeFromSSE();
    } catch (_) {}
    _subscription = null;
  }
}


