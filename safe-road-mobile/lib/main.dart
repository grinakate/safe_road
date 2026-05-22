import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:provider/provider.dart';
import 'package:go_router/go_router.dart';
import 'package:safe_road/screens/main_screen.dart';
import 'package:safe_road/screens/register_screen.dart';
import 'package:safe_road/screens/topic_screen.dart';
import 'package:safe_road/screens/quiz_screen.dart';
import 'package:safe_road/screens/result_screen.dart';
import 'package:safe_road/screens/profile_screen.dart';
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
      child: Builder(builder: (context) {
        // Build GoRouter here so we can access providers (AuthProvider) from context
        final auth = context.watch<AuthProvider>();
        final router = GoRouter(
          initialLocation: initialRoute,
          navigatorKey: getIt<GlobalKey<NavigatorState>>(),
          refreshListenable: auth,
          redirect: (context, state) {
            final loggedIn = auth.isAuthenticated;
            final loggingIn = state.location == '/login' || state.location == '/register';
            // If not logged in, redirect to login for protected routes
            if (!loggedIn && !loggingIn) return '/login';
            // If logged in and visiting auth pages, go to map
            if (loggedIn && loggingIn) return '/map';
            return null;
          },
          routes: [
            GoRoute(path: '/', redirect: (context, state) => initialRoute),
            GoRoute(path: '/login', builder: (context, state) => LoginScreen()),
            GoRoute(path: '/register', builder: (context, state) => RegisterScreen()),
            GoRoute(path: '/map', builder: (context, state) => MainScreen()),
            GoRoute(path: '/leaderboard', builder: (context, state) => LeaderboardScreen()),
            GoRoute(
              path: '/topic/:id',
              builder: (context, state) {
                final idStr = state.pathParameters['id'] ?? '';
                final id = int.tryParse(idStr);
                if (id == null) return Scaffold(body: Center(child: Text('Invalid topic id')));
                return TopicScreen(topicId: id);
              },
            ),
            GoRoute(
              path: '/quiz/:sessionId',
              builder: (context, state) {
                final sessionId = state.pathParameters['sessionId'] ?? '';
                return QuizScreen(sessionId: sessionId);
              },
            ),
            GoRoute(path: '/result', builder: (context, state) => ResultScreen()),
            GoRoute(path: '/profile', builder: (context, state) => ProfileScreen()),
          ],
          errorBuilder: (context, state) => Scaffold(body: Center(child: Text('Page not found: ${state.location}'))),
        );

        return MaterialApp.router(
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
          routerDelegate: router.routerDelegate,
          routeInformationParser: router.routeInformationParser,
          routeInformationProvider: router.routeInformationProvider,
          title: 'Безопасная дорога',
          debugShowCheckedModeBanner: false,
          theme: AppTheme.lightTheme,
          // navigatorKey is provided to GoRouter above so NotificationService can show dialogs
        );
      }),
    );
  }
}
