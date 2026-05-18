import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

import '../core/constants.dart';
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

  http.Client? _httpClient;
  StreamSubscription<String>? _subscription;

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
    _httpClient = http.Client();
    final uri = Uri.parse(
      '${AppConstants.baseUrl}${ApiV1Client.apiVersion}/notifications/subscribe',
    );

    try {
      final headers = await _apiClient.getAuthHeaders();
      final request = http.Request('GET', uri);
      request.headers.addAll(headers);

      final streamed = await _httpClient!.send(request);

      // преобразуем байтовый поток в строки и парсим SSE-формат простым образом
      _subscription = streamed.stream
          .transform(utf8.decoder)
          .listen(
            (chunk) {
              _handleChunk(chunk);
            },
            onDone: () {
              // Попробуем переподключиться через небольшую паузу
              Future.delayed(Duration(seconds: 2), _startListening);
            },
            onError: (err) {
              Future.delayed(Duration(seconds: 2), _startListening);
            },
            cancelOnError: true,
          );
    } catch (e) {
      // reconnect
      Future.delayed(Duration(seconds: 2), _startListening);
    }
  }

  String _buffer = '';

  void _handleChunk(String chunk) {
    _buffer += chunk;

    // SSE использует разделитель '\n\n' между событиями
    while (_buffer.contains('\n\n')) {
      final idx = _buffer.indexOf('\n\n');
      final raw = _buffer.substring(0, idx);
      _buffer = _buffer.substring(idx + 2);

      final lines = raw.split('\n');
      String eventName = '';
      String data = '';
      for (var line in lines) {
        if (line.startsWith('event:')) {
          eventName = line.substring(6).trim();
        } else if (line.startsWith('data:')) {
          data += line.substring(5).trim();
        }
      }

      if (data.isNotEmpty) {
        final evt = NotificationEvent.fromEvent(
          eventName.isEmpty ? 'notification' : eventName,
          data,
        );
        _onEvent(evt);
      }
    }
  }

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
        await _showDialogFor(evt);
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
        return AlertDialog(
          title: Text('Вы получили награду!'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (levelUp) ...[
                Text('Поздравляем! Вы повысили уровень до $newLevel'),
                SizedBox(height: 8),
              ],
              Text('Получено опыта: $earnedXp'),
              SizedBox(height: 8),
              Text('Всего опыта: $totalXp'),
              SizedBox(height: 12),
              Image.asset(
                'assets/images/congratulations.png',
                width: 120,
                height: 120,
                errorBuilder: (c, e, s) =>
                    Icon(Icons.card_giftcard, size: 64, color: Colors.amber),
              ),
            ],
          ),
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

  void dispose() {
    _subscription?.cancel();
    _httpClient?.close();
  }
}
