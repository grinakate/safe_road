import 'package:flutter/material.dart';

import '../core/service_locator.dart';
import '../models/auth_models.dart';
import '../services/auth_service.dart';

class RegisterScreen extends StatefulWidget {
  @override
  _RegisterScreenState createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _authService = getIt<AuthService>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  DateTime _birthDate = DateTime(2010, 1, 1);
  bool _isLoading = false;

  void _register() async {
    setState(() => _isLoading = true);

    final request = UserRegisterRequest(
      name: _nameController.text,
      email: _emailController.text,
      password: _passwordController.text,
      birthDate: _birthDate,
      avatarId: 1,
    );

    final token = await _authService.register(request);
    setState(() => _isLoading = false);

    if (token != null) {
      Navigator.pushReplacementNamed(context, '/map');
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Ошибка регистрации. Возможно, email занят.'),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Регистрация")),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(labelText: 'Имя'),
            ),
            TextField(
              controller: _emailController,
              decoration: const InputDecoration(labelText: 'Email'),
            ),
            TextField(
              controller: _passwordController,
              decoration: const InputDecoration(labelText: 'Пароль'),
              obscureText: true,
            ),
            const SizedBox(height: 16),
            ListTile(
              title: Text(
                "Дата рождения: ${_birthDate.toLocal()}".split(' ')[0],
              ),
              trailing: const Icon(Icons.calendar_today),
              onTap: () async {
                DateTime? picked = await showDatePicker(
                  context: context,
                  initialDate: _birthDate,
                  firstDate: DateTime(1900),
                  lastDate: DateTime.now(),
                );
                if (picked != null) setState(() => _birthDate = picked);
              },
            ),
            const SizedBox(height: 24),
            _isLoading
                ? const CircularProgressIndicator()
                : ElevatedButton(
                    onPressed: _register,
                    child: const Text('Зарегистрироваться'),
                  ),
          ],
        ),
      ),
    );
  }
}
