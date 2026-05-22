import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/screens/main_screen.dart';
import 'package:safe_road/screens/register_screen.dart';
import 'package:safe_road/theme/app_theme.dart';

import 'core/service_locator.dart';
import 'services/auth_service.dart';
import 'screens/login_screen.dart';
import 'providers/auth_provider.dart';
import 'providers/notification_provider.dart';
import 'providers/game_profile_provider.dart';
import 'providers/learning_provider.dart';
import 'providers/leaderboard_provider.dart';
import 'providers/topic_provider.dart';
import 'screens/leaderboard_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  setupLocator();
  final bool isAuth = await getIt<AuthService>().isAuthenticated();
  runApp(SafeRoadApp(initialRoute: isAuth ? '/map' : '/login', isAuth: isAuth));
}

class SafeRoadApp extends StatelessWidget {
  final String initialRoute;
  final bool isAuth;
  const SafeRoadApp({super.key, required this.initialRoute, required this.isAuth});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider()),
        ChangeNotifierProvider(create: (_) => GameProfileProvider()),
        ChangeNotifierProvider(create: (_) => LearningProvider()),
        ChangeNotifierProvider(create: (_) => LeaderboardProvider()),
        ChangeNotifierProvider(create: (_) => TopicProvider()),
        // NotificationProvider: используем единственный экземпляр, доступный через GetIt
        ChangeNotifierProvider<NotificationProvider>(
          create: (_) => getIt<NotificationProvider>(),
        ),
      ],
      child: MaterialApp(
      // Поддержка локалей нужна для корректной работы системных виджетов локализации
      localizationsDelegates: const [
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: const [
        Locale('ru'),
        Locale('en'),
      ],
      navigatorKey: getIt<GlobalKey<NavigatorState>>(),
      title: 'Безопасная дорога',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      initialRoute: initialRoute,
        routes: {
          '/register': (context) => RegisterScreen(),
          '/login': (context) => LoginScreen(),
          '/map': (context) => MainScreen(),
          '/leaderboard': (context) => LeaderboardScreen(),
        },
      ),
    );
  }
}
