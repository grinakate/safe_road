import 'package:flutter/material.dart';

class WelcomeScreen extends StatelessWidget {
  // Если вы хотите отображать имя пользователя, которое пришло с бэкенда
  // final String username;
  // const WelcomeScreen({super.key, required this.username});

  const WelcomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // Если вы получаете данные пользователя, можно использовать их здесь
    // final user = ModalRoute.of(context)!.settings.arguments as UserModel;
    // final String username = user.username;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Добро пожаловать!'),
        // Можно убрать кнопку "Назад", если не хотим, чтобы пользователь возвращался к регистрации
        automaticallyImplyLeading: false,
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Icon(Icons.waving_hand, size: 80, color: Theme.of(context).primaryColor),
              const SizedBox(height: 32),
              Text(
                // 'Привет, $username!', // Если используете имя пользователя
                'Добро пожаловать!',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),
              const Text(
                'Ваш email отправлен на почту. Пожалуйста, подтвердите его, чтобы продолжить.',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 18, color: Colors.grey),
              ),
              const SizedBox(height: 48),
              ElevatedButton(
                onPressed: () {
                  // Здесь может быть кнопка, которая ведет на главный экран приложения
                  // или на экран ввода логина, если нужно заново авторизоваться
                  // Для примера, просто перенаправим на экран логина
                  Navigator.of(context).pushReplacementNamed('/login'); // Предполагается, что есть экран логина
                },
                style: ElevatedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 32),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                ),
                child: const Text(
                  'Перейти к входу',
                  style: TextStyle(fontSize: 18),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
