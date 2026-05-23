import 'package:flutter/widgets.dart';

import 'notification_provider.dart';

class NotificationNavigatorObserver extends NavigatorObserver {
  final NotificationProvider provider;

  NotificationNavigatorObserver(this.provider);

  @override
  void didPop(Route route, Route? previousRoute) {
    provider.checkAndFlushQueue();
  }
}
