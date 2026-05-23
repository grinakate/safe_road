import 'package:flutter/material.dart';

import '../../core/service_locator.dart'; // Для getIt
import '../../data/services/notification_service.dart';
import '../../ui/widgets/dialogs/notification_dialogs.dart';
import 'game_profile_provider.dart';

class NotificationProvider extends ChangeNotifier {
  final NotificationService _service;
  final GlobalKey<NavigatorState> _navigatorKey;
  final GameProfileProvider _gameProfileProvider = getIt<GameProfileProvider>();

  // Флаг занятости пользователя
  bool _isBusy = false;

  bool get isBusy => _isBusy;

  // Очередь уведомлений, если пользователь занят
  final List<NotificationEvent> _queue = [];

  bool _isDisposed = false;

  late final Map<String, Function(BuildContext, NotificationEvent)> _handlers;

  NotificationProvider(this._service, this._navigatorKey) {
    _handlers = {'REWARDS_EARNED': NotificationDialogs.showRewardDialog};

    _service.events.listen((evt) {
      _addNotificationToQueueOrHandle(evt);
    });
  }

  void setBusy(bool busy) {
    _isBusy = busy;
    if (!_isBusy) {
      _flushQueue(); // Если пользователь больше не занят, обрабатываем очередь
    }
    _safeNotify(); // Уведомляем слушателей об изменении состояния
  }

  void _addNotificationToQueueOrHandle(NotificationEvent evt) {
    if (_isBusy) {
      _queue.add(evt);
      return;
    }
    _handleEvent(evt);
  }

  void _handleEvent(NotificationEvent evt) {
    final context = _navigatorKey.currentState?.context;
    if (context == null || !context.mounted) return;

    // Специфическая логика для "REWARDS_EARNED" (обновление профиля)
    if (evt.title == 'REWARDS_EARNED') {
      _gameProfileProvider.handleRewardEvent(evt.content);
    }

    // Выбираем и вызываем нужный диалог
    final showDialogAction =
        _handlers[evt.title] ?? NotificationDialogs.showSimpleDialog;
    showDialogAction(context, evt);
  }

  // Обработка очереди уведомлений
  Future<void> _flushQueue() async {
    if (_queue.isEmpty) return;
    while (_queue.isNotEmpty) {
      final evt = _queue.removeAt(0);
      await Future.delayed(const Duration(milliseconds: 200));
      await Future.microtask(() => _handleEvent(evt));
    }
  }

  void _safeNotify() {
    if (!_isDisposed) {
      notifyListeners();
    }
  }

  @override
  void dispose() {
    _isDisposed = true;
    super.dispose();
  }
}
