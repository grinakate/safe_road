import 'package:flutter/material.dart';
import 'package:safe_road/screens/main_screen.dart';
import 'package:safe_road/screens/register_screen.dart';
import 'package:safe_road/theme/app_theme.dart';

import 'core/service_locator.dart';
import 'screens/login_screen.dart';
import 'services/auth_service.dart';
import 'services/notification_service.dart';
import 'package:flutter/material.dart';

// navigatorKey registered in service locator
// navigatorKey берётся из service_locator через getIt

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  setupLocator();
  final bool isAuth = await getIt<AuthService>().isAuthenticated();
  if (isAuth) {
    try {
      getIt<NotificationService>().start();
    } catch (_) {}
  }
  runApp(SafeRoadApp(initialRoute: isAuth ? '/map' : '/login'));
}

class SafeRoadApp extends StatelessWidget {
  final String initialRoute;
  const SafeRoadApp({super.key, required this.initialRoute});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorKey: getIt<GlobalKey<NavigatorState>>(),
      title: 'Безопасная дорога',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      initialRoute: initialRoute,
      routes: {
        '/register': (context) => RegisterScreen(),
        '/login': (context) => LoginScreen(),
        '/map': (context) => MainScreen(),
      },
    );
  }
}
