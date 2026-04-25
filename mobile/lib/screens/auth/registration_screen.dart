import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/services/auth_service.dart'; // Для использования Provider или ChangeNotifierProvider
import 'package:intl/intl.dart';

class RegistrationScreen extends StatefulWidget {
  const RegistrationScreen({super.key});

  @override
  State<RegistrationScreen> createState() => _RegistrationScreenState();
}

class _RegistrationScreenState extends State<RegistrationScreen> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _birthDateController = TextEditingController();
  DateTime? _selectedDate;
  bool _isLoading = false;

  @override
  void dispose() {
    _usernameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _birthDateController.dispose();
    super.dispose();
  }

  // Функция для выбора даты
  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate ?? DateTime.now(),
      firstDate: DateTime(1900),
      // Самая ранняя возможная дата
      lastDate: DateTime.now(),
      builder: (BuildContext context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            // Настройка темы для DatePicker
            primaryColor: Theme.of(context).primaryColor,
            colorScheme: ColorScheme.light(
              primary: Theme.of(context).primaryColor,
            ),
            buttonTheme: const ButtonThemeData(
              textTheme: ButtonTextTheme.primary,
            ),
          ),
          child: child!,
        );
      },
    );
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
        _birthDateController.text = DateFormat('dd.MM.yyyy').format(picked);
      });
    }
  }

  Future<void> _submitRegistration() async {
    if (_formKey.currentState!.validate()) {
      if (_selectedDate == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Пожалуйста, выберите дату рождения')),
        );
        return;
      }

      setState(() {
        _isLoading = true;
      });

      try {
        final authService = Provider.of<AuthService>(context, listen: false);
        await authService.register(
          name: _usernameController.text,
          email: _emailController.text,
          password: _passwordController.text,
          birthDate: _selectedDate!,
        );

        // Успешная регистрация, перенаправляем на экран приветствия
        // Важно: Если бэк не вернул UserModel, тут может быть небольшой нюанс
        // Если бэк просто подтверждает успех, то мы можем просто перейти.
        // Если для приветственного экрана нужны данные пользователя,
        // нужно будет либо получать их отдельно, либо адаптировать API.
        Navigator.of(
          context,
        ).pushReplacementNamed('/welcome'); // Используйте именованные маршруты
      } catch (e) {
        // Показать сообщение об ошибке пользователю
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              'Ошибка регистрации: ${e.toString().replaceFirst('Exception: ', '')}',
            ),
          ),
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
      appBar: AppBar(title: const Text('Регистрация')),
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
                  'Добро пожаловать!',
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 48),
                // Поле для имени пользователя
                TextFormField(
                  controller: _usernameController,
                  decoration: InputDecoration(
                    labelText: 'Имя пользователя',
                    border: OutlineInputBorder(),
                    prefixIcon: Icon(Icons.person),
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'Пожалуйста, введите имя пользователя';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // Поле для email
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
                    // Простая валидация email
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
                    if (value.length < 6) {
                      return 'Пароль должен содержать не менее 6 символов';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 24),

                // Поле для даты рождения
                TextFormField(
                  controller: _birthDateController,
                  readOnly: true,
                  // Делаем поле только для чтения, выбор даты будет по кнопке
                  decoration: InputDecoration(
                    labelText: 'Дата рождения',
                    border: OutlineInputBorder(),
                    prefixIcon: Icon(Icons.calendar_today),
                    suffixIcon: IconButton(
                      // Кнопка для вызова DatePicker
                      icon: Icon(Icons.calendar_today),
                      onPressed: () => _selectDate(context),
                    ),
                  ),
                  validator: (value) {
                    if (_selectedDate == null) {
                      return 'Пожалуйста, выберите дату рождения';
                    }
                    return null;
                  },
                  onTap: () =>
                      _selectDate(context), // Тоже вызываем при нажатии на поле
                ),
                const SizedBox(height: 24),

                // Кнопка регистрации
                _isLoading
                    ? const Center(child: CircularProgressIndicator())
                    : ElevatedButton(
                        onPressed: _submitRegistration,
                        // ... (стили кнопки) ...
                        child: const Text(
                          'Зарегистрироваться',
                          style: TextStyle(fontSize: 18),
                        ),
                      ),
                const SizedBox(height: 16),
                // Ссылка на логин (если есть)
                TextButton(
                  onPressed: () {
                    Navigator.of(context).pushReplacementNamed(
                      '/login',
                    ); // Предполагается, что у вас есть экран логина
                  },
                  child: const Text('Уже есть аккаунт? Войти'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
