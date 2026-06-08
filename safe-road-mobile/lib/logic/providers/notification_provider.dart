import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:safe_road/data/models/gamification/achievement_notification.dart';

import '../../core/service_locator.dart'; // Для getIt
import '../../data/services/notification_service.dart';
import '../../ui/widgets/dialogs/notification_dialogs.dart';
import 'game_profile_provider.dart';

class NotificationProvider extends ChangeNotifier {
  final NotificationService _service;
  final GlobalKey<NavigatorState> _navigatorKey;
  final GameProfileProvider _gameProfileProvider;

  // Очередь уведомлений, если пользователь занят
  final List<NotificationEvent> _queue = [];

  bool _isDisposed = false;

  late final Map<String, Function(BuildContext, NotificationEvent)> _handlers;

  NotificationProvider(
    this._service,
    this._gameProfileProvider,
    this._navigatorKey,
  ) {
    _handlers = {
      'REWARDS_EARNED': NotificationDialogs.showRewardDialog,
      'NEW_ACHIEVEMENT': NotificationDialogs.showAchievementDialog,
    };

    _service.events.listen((evt) {
      _addNotificationToQueueOrHandle(evt);
    });
  }

  void checkAndFlushQueue() {
    if (_canShowNotifications && _queue.isNotEmpty) {
      _flushQueue();
    }
  }

  void _addNotificationToQueueOrHandle(NotificationEvent evt) {
    if (!_canShowNotifications) {
      _queue.add(evt);
      return;
    }
    _handleEvent(evt);
  }

  Future<void> _handleEvent(NotificationEvent evt) async {
    final context = _navigatorKey.currentState?.context;
    if (context == null || !context.mounted) return;

    if (evt.title == 'REWARDS_EARNED') {
      _gameProfileProvider.handleRewardEvent(evt.content);
    }
    if (evt.title == 'NEW_ACHIEVEMENT') {
      _gameProfileProvider.handleAchievementEvent(evt.content);
    }

    final showDialogAction =
        _handlers[evt.title] ?? NotificationDialogs.showSimpleDialog;

    await showDialogAction(context, evt);
  }


  bool get _canShowNotifications {
    final navigatorKey = getIt<GlobalKey<NavigatorState>>();
    final context = navigatorKey.currentContext;

    if (context == null) {
      return true;
    }

    try {
      final router = GoRouter.of(context);
      final String location =
          router.routerDelegate.currentConfiguration.last.matchedLocation;

      return location == '/main';
    } catch (e) {
      debugPrint('Ошибка при проверке пути в NotificationProvider: $e');
      return true; // В случае ошибки разрешаем, чтобы не сломать приложение
    }
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

  void clear() {
    _queue.clear();
    _safeNotify();
  }
}
