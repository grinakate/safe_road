import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'screens/auth/login_screen.dart';
import 'screens/auth/registration_screen.dart';
import 'screens/auth/welcome_screen.dart';
import 'services/auth_service.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider( // Используйте MultiProvider, если у вас несколько провайдеров
      providers: [
        // Предоставляем AuthService, чтобы его можно было использовать в виджетах
        Provider<AuthService>(
          create: (_) => AuthService(),
        ),
        // Здесь могут быть другие провайдеры (например, для управления состоянием)
      ],
      child: MaterialApp(
        title: 'Безопасная дорога',
        theme: ThemeData(
          primarySwatch: Colors.blue,
          visualDensity: VisualDensity.adaptivePlatformDensity,
          // Дополнительные настройки темы
          elevatedButtonTheme: ElevatedButtonThemeData(
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.blueAccent, // Пример цвета
              foregroundColor: Colors.white,
            ),
          ),
        ),
        home: const AuthChecker(), // Стартовый маршрут
        routes: {
          '/register': (context) => const RegistrationScreen(),
          '/welcome': (context) => const WelcomeScreen(), // Экран приветствия после регистрации
          '/login': (context) => const LoginScreen(), // Экран логина (предполагается, что он есть)
          '/home': (context) => const HomeScreen(),
        },
      ),
    );
  }
}

class AuthChecker extends StatefulWidget {
  const AuthChecker({super.key});

  @override
  State<AuthChecker> createState() => _AuthCheckerState();
}

class _AuthCheckerState extends State<AuthChecker> {
  bool _isChecking = true;
  bool _isLoggedIn = false;

  @override
  void initState() {
    super.initState();
    _checkAuthStatus();
  }

  Future<void> _checkAuthStatus() async {
    // Получаем AuthService из контекста
    final authService = Provider.of<AuthService>(context, listen: false);
    final loggedIn = await authService.isLoggedIn(); // Проверяем наличие токена

    if (mounted) { // Убедимся, что виджет еще в дереве
      setState(() {
        _isLoggedIn = loggedIn;
        _isChecking = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isChecking) {
      // Пока идет проверка, показываем сплэш-скрин или индикатор загрузки
      return const Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    } else {
      // После проверки, показываем соответствующий экран
      if (_isLoggedIn) {
        // Если пользователь авторизован, ведем на главный экран
        return const HomeScreen();
      } else {
        // Если не авторизован, ведем на экран логина
        return const LoginScreen();
      }
    }
  }
}

// Заглушка HomeScreen для демонстрации
class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Главный экран'),
      ),
      body: const Center(
        child: Text('Добро пожаловать в главное меню!'),
      ),
    );
  }
}
