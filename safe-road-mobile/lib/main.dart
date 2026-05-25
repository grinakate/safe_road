import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/core/service_locator.dart';
import 'package:safe_road/logic/providers/auth_provider.dart';
import 'package:safe_road/ui/screens/auth/login_screen.dart';
import 'package:safe_road/ui/screens/main_screen.dart';
import 'package:safe_road/ui/screens/profile/profile_screen.dart';
import 'package:safe_road/ui/screens/learning/quiz_screen.dart';
import 'package:safe_road/ui/screens/auth/register_screen.dart';
import 'package:safe_road/ui/screens/learning/result_screen.dart';
import 'package:safe_road/ui/screens/learning/topic_screen.dart';
import 'package:safe_road/ui/theme/app_theme.dart';

import 'logic/providers/game_profile_provider.dart';
import 'logic/providers/leaderboard_provider.dart';
import 'logic/providers/learning_provider.dart';
import 'logic/providers/notification_navigator_observer.dart';
import 'logic/providers/notification_provider.dart';
import 'logic/providers/topic_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  setupLocator();

  // Запускаем приложение
  runApp(const SafeRoadApp());
}

class SafeRoadApp extends StatelessWidget {
  const SafeRoadApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => getIt<AuthProvider>()),
        ChangeNotifierProvider(create: (_) => getIt<GameProfileProvider>()),
        ChangeNotifierProvider(create: (_) => getIt<LearningProvider>()),
        ChangeNotifierProvider(create: (_) => getIt<LeaderboardProvider>()),
        ChangeNotifierProvider(create: (_) => getIt<TopicProvider>()),
        ChangeNotifierProvider(create: (_) => getIt<NotificationProvider>()),
      ],
      child: Builder(
        builder: (context) {
          final auth = context.watch<AuthProvider>();

          final router = GoRouter(
            initialLocation: '/',
            navigatorKey: getIt<GlobalKey<NavigatorState>>(),
            refreshListenable: auth,
            observers: [
              NotificationNavigatorObserver(getIt<NotificationProvider>()),
            ],
            redirect: (context, state) {
              final isLoggedIn = auth.isAuthenticated;
              final isLoggingInOrRegistering =
                  state.location == '/login' || state.location == '/register';

              // 1. Если не авторизован и пытается получить доступ к защищенному маршруту (не login/register)
              if (!isLoggedIn && !isLoggingInOrRegistering) {
                return '/login';
              }
              // 2. Если авторизован и пытается зайти на страницы логина/регистрации
              if (isLoggedIn && isLoggingInOrRegistering) {
                return '/main';
              }
              // 3. Если авторизован и находится на корневом маршруте
              if (isLoggedIn && state.location == '/') {
                return '/main';
              }
              // 4. Если не авторизован и находится на корневом маршруте
              if (!isLoggedIn && state.location == '/') {
                return '/login';
              }

              // В остальных случаях (авторизован на защищенном маршруте, не авторизован на логине/регистрации)
              return null;
            },
            routes: [
              GoRoute(
                path: '/login',
                builder: (context, state) => const LoginScreen(),
              ),
              GoRoute(
                path: '/register',
                builder: (context, state) => const RegisterScreen(),
              ),
              GoRoute(
                path: '/main',
                builder: (context, state) => const MainScreen(),
              ),
              GoRoute(
                path: '/topic/:id',
                builder: (context, state) {
                  final idStr = state.pathParameters['id'];
                  final id = int.tryParse(idStr ?? '');
                  if (id == null) {
                    return const Scaffold(
                      body: Center(child: Text('Неверный ID темы')),
                    );
                  }
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
              GoRoute(
                path: '/result',
                builder: (context, state) => const ResultScreen(),
              ),
              GoRoute(
                path: '/profile',
                builder: (context, state) => const ProfileScreen(),
              ),
            ],
            // Обработчик ошибок для несуществующих маршрутов
            errorBuilder: (context, state) => Scaffold(
              body: Center(
                child: Text('Страница не найдена: ${state.location}'),
              ),
            ),
          );

          return MaterialApp.router(
            localizationsDelegates: const [
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
              GlobalCupertinoLocalizations.delegate,
            ],
            supportedLocales: const [Locale('ru'), Locale('en')],
            routerDelegate: router.routerDelegate,
            routeInformationParser: router.routeInformationParser,
            routeInformationProvider: router.routeInformationProvider,
            title: 'Безопасная дорога',
            debugShowCheckedModeBanner: false,
            theme: AppTheme.lightTheme,
          );
        },
      ),
    );
  }
}
