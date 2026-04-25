import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/services/auth_service.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  bool _isLoading = false;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _submitLogin() async {
    if (_formKey.currentState!.validate()) {
      setState(() {
        _isLoading = true;
      });

      try {
        final authService = Provider.of<AuthService>(context, listen: false);
        // Предполагаем, что ваш AuthService имеет метод login.
        // Этот метод должен проверить email/password и вернуть UserModel,
        // если логин успешен, или выбросить исключение, если нет.
        // Важно: для логина может понадобиться отдельный DTO, если бэк возвращает
        // токен и/или дополнительные данные пользователя, а не целый UserModel.
        // Адаптируйте под ваш API.

        // Пример вызова (если метод login существует):
        // final UserModel user = await authService.login(
        //   email: _emailController.text,
        //   password: _passwordController.text,
        // );

        // Пока, для демонстрации, имитируем успешный вход
        // В реальном приложении здесь будет вызов AuthService.login()
        await authService.login(
            email: _emailController.text,
            password: _passwordController.text,
          );

        // После успешного логина, вам нужно будет сохранить токен доступа (если есть)
        // и/или данные пользователя, чтобы приложение знало, что пользователь авторизован.
        // Например, используя Provider, SharedPreferences, или другой менеджер состояния.

        // После успешного логина перенаправляем на главный экран приложения
        // Предполагаем, что у вас есть маршрут '/home'
        Navigator.of(context).pushReplacementNamed('/home');

      } catch (e) {
        // Показать сообщение об ошибке пользователю
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Ошибка входа: ${e.toString().replaceFirst('Exception: ', '')}')),
        );
      } finally {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Вход'),
      ),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24.0),
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                const Text(
                  'С возвращением!',
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 48),

                // Поле для Email
                TextFormField(
                  controller: _emailController,
                  decoration: InputDecoration(
                    labelText: 'Email',
                    border: OutlineInputBorder(),
                    prefixIcon: Icon(Icons.email),
                  ),
                  keyboardType: TextInputType.emailAddress,
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'Пожалуйста, введите ваш email';
                    }
                    if (!value.contains('@')) {
                      return 'Введите корректный email';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // Поле для пароля
                TextFormField(
                  controller: _passwordController,
                  decoration: InputDecoration(
                    labelText: 'Пароль',
                    border: OutlineInputBorder(),
                    prefixIcon: Icon(Icons.lock),
                  ),
                  obscureText: true,
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'Пожалуйста, введите пароль';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 24),

                // Кнопка входа
                _isLoading
                    ? const Center(child: CircularProgressIndicator())
                    : ElevatedButton(
                  onPressed: _submitLogin,
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                  ),
                  child: const Text(
                    'Войти',
                    style: TextStyle(fontSize: 18),
                  ),
                ),
                const SizedBox(height: 16),

                // Ссылка на регистрацию
                TextButton(
                  onPressed: () {
                    Navigator.of(context).pushReplacementNamed('/register'); // Переход на экран регистрации
                  },
                  child: const Text('Нет аккаунта? Зарегистрироваться'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
