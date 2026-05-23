import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import '../../logic/providers/auth_provider.dart';
import '../theme/app_theme.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isPasswordVisible = false;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _login() async {
    if (!(_formKey.currentState?.validate() ?? false)) return;

    try {
      final authProvider = context.read<AuthProvider>();

      final token = await authProvider.login(
        _emailController.text,
        _passwordController.text,
      );

      if (token != null && mounted) {
        context.go('/main');
      }
    } catch (e) {
      // Ловим ошибку и выводим пользователю понятный текст ошибки из Провайдера
      if (mounted) {
        final errorMsg =
            context.read<AuthProvider>().errorMessage ??
            'Неверный логин или пароль';
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text(errorMsg)));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    // Слушаем статус загрузки напрямую из состояния провайдера
    final isSubmitting = context.select<AuthProvider, bool>((p) => p.isLoading);

    return Scaffold(
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                _buildHeader(),
                const SizedBox(height: 40),
                _buildLoginForm(),
                const SizedBox(height: 24),
                _buildLoginButton(isSubmitting),
                const SizedBox(height: 16),
                _buildSignUpLink(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return const Column(
      children: [
        Text(
          'Добро пожаловать!',
          style: AppTheme.headerTextStyle,
          textAlign: TextAlign.center,
        ),
        SizedBox(height: 8),
        Text(
          'Войдите в свой аккаунт, чтобы продолжить',
          style: AppTheme.subHeaderTextStyle,
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  Widget _buildLoginForm() {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          // Поле Email
          TextFormField(
            controller: _emailController,
            keyboardType: TextInputType.emailAddress,
            decoration: AppTheme.inputDecoration.copyWith(
              labelText: 'Email',
              hintText: 'Введите email',
              prefixIcon: const Icon(
                Icons.email_outlined,
                color: AppColors.darkBrownIcon,
              ),
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Введите email';
              }
              if (!RegExp(r'^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(value)) {
                return 'Введите корректный email';
              }
              return null;
            },
          ),
          const SizedBox(height: 16),

          // Поле Пароля
          TextFormField(
            controller: _passwordController,
            obscureText: !_isPasswordVisible,
            decoration: AppTheme.inputDecoration.copyWith(
              labelText: 'Пароль',
              hintText: 'Введите пароль',
              prefixIcon: const Icon(
                Icons.lock_outline,
                color: AppColors.darkBrownIcon,
              ),
              suffixIcon: IconButton(
                icon: Icon(
                  _isPasswordVisible ? Icons.visibility_off : Icons.visibility,
                  color: AppColors.darkBrownIcon,
                ),
                onPressed: () {
                  setState(() {
                    _isPasswordVisible = !_isPasswordVisible;
                  });
                },
              ),
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'Введите пароль';
              }
              if (value.length < 6) {
                return 'Пароль должен быть не менее 6 символов';
              }
              return null;
            },
          ),
          const SizedBox(height: 8),

          // Ссылка "Забыли пароль?"
          Align(
            alignment: Alignment.centerRight,
            child: TextButton(
              onPressed: () {
                // Логика восстановления пароля (если планируется бэкендом)
              },
              child: const Text(
                'Забыли пароль?',
                style: AppTextStyles.bodyMedium,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLoginButton(bool isLoading) {
    return ElevatedButton(
      onPressed: isLoading ? null : _login,
      style: ElevatedButton.styleFrom(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        padding: const EdgeInsets.symmetric(vertical: 14),
      ),
      child: isLoading
          ? const SizedBox(
              height: 20,
              width: 20,
              child: CircularProgressIndicator(
                strokeWidth: 2,
                valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
              ),
            )
          : const Text('Войти', style: AppTextStyles.buttonText),
    );
  }

  Widget _buildSignUpLink() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Text("Еще нет аккаунта?", style: AppTextStyles.bodyMedium),
        TextButton(
          onPressed: () => context.push('/register'),
          child: const Text(
            'Зарегистрироваться',
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
              color: AppColors.primaryGreen,
            ),
          ),
        ),
      ],
    );
  }
}
