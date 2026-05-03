import 'package:flutter/material.dart';
import 'package:safe_road/screens/main_screen.dart';
import 'package:safe_road/screens/register_screen.dart';

import 'core/service_locator.dart';
import 'screens/login_screen.dart';
import 'services/auth_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  setupLocator();
  final bool isAuth = await getIt<AuthService>().isAuthenticated();
  runApp(SafeRoadApp(initialRoute: isAuth ? '/map' : '/login'));
}

class SafeRoadApp extends StatelessWidget {
  final String initialRoute;
  const SafeRoadApp({super.key, required this.initialRoute});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Безопасная дорога',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(useMaterial3: true, colorSchemeSeed: Colors.green),
      initialRoute: initialRoute,
      routes: {
        '/register': (context) => RegisterScreen(),
        '/login': (context) => LoginScreen(),
        '/map': (context) => MainScreen(),
      },
    );
  }
}
