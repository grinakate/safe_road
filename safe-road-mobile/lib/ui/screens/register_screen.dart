import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../../logic/providers/auth_provider.dart';
import '../theme/app_theme.dart';
import '../../utils/date_picker_helper.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();
  final _birthDateController = TextEditingController();

  bool _isPasswordVisible = false;
  bool _isConfirmPasswordVisible = false;
  DateTime? _birthDate;

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    _birthDateController.dispose();
    super.dispose();
  }

  Future<void> _register() async {
    if (!(_formKey.currentState?.validate() ?? false)) return;
    if (_birthDate == null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Выберите дату рождения')));
      return;
    }

    try {
      final authProvider = context.read<AuthProvider>();
      final token = await authProvider.register(
        nickname: _nameController.text,
        email: _emailController.text,
        birthDate: _birthDate!,
        password: _passwordController.text,
      );

      if (token != null && mounted) {
        context.go('/main');
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              context.read<AuthProvider>().errorMessage ?? 'Ошибка регистрации',
            ),
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    // Слушаем статус авторизации для управления лоадером
    final isSubmitting = context.select<AuthProvider, bool>((p) => p.isLoading);

    return Scaffold(
      appBar: AppBar(title: const Text("Регистрация")),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            const Text('Регистрация', style: AppTheme.headerTextStyle),
            const SizedBox(height: 8),
            const Text(
              'Заполните данные, чтобы продолжить',
              style: AppTheme.subHeaderTextStyle,
            ),
            const SizedBox(height: 40),
            _buildRegisterForm(),
            const SizedBox(height: 32),
            _buildRegisterButton(isSubmitting),
          ],
        ),
      ),
    );
  }

  Widget _buildRegisterForm() => Form(
    key: _formKey,
    child: Column(
      children: [
        _buildTextField(_nameController, 'Имя', Icons.person, false),
        const SizedBox(height: 16),
        _buildTextField(
          _emailController,
          'Email',
          Icons.email_outlined,
          false,
          keyboardType: TextInputType.emailAddress,
        ),
        const SizedBox(height: 16),
        _buildDatePickerField(),
        const SizedBox(height: 16),
        _buildPasswordField(
          _passwordController,
          'Пароль',
          _isPasswordVisible,
          () => setState(() => _isPasswordVisible = !_isPasswordVisible),
        ),
        const SizedBox(height: 16),
        _buildPasswordField(
          _confirmPasswordController,
          'Повторите пароль',
          _isConfirmPasswordVisible,
          () => setState(
            () => _isConfirmPasswordVisible = !_isConfirmPasswordVisible,
          ),
          isConfirm: true,
        ),
      ],
    ),
  );

  Widget _buildTextField(
    TextEditingController controller,
    String label,
    IconData icon,
    bool obscure, {
    TextInputType? keyboardType,
  }) => TextFormField(
    controller: controller,
    obscureText: obscure,
    keyboardType: keyboardType,
    decoration: AppTheme.inputDecoration.copyWith(
      labelText: label,
      prefixIcon: Icon(icon, color: AppColors.darkBrownIcon),
    ),
    validator: (value) => (value?.isEmpty ?? true) ? 'Введите $label' : null,
  );

  Widget _buildPasswordField(
    TextEditingController controller,
    String label,
    bool isVisible,
    VoidCallback toggle, {
    bool isConfirm = false,
  }) => TextFormField(
    controller: controller,
    obscureText: !isVisible,
    decoration: AppTheme.inputDecoration.copyWith(
      labelText: label,
      prefixIcon: const Icon(
        Icons.lock_outline,
        color: AppColors.darkBrownIcon,
      ),
      suffixIcon: IconButton(
        icon: Icon(isVisible ? Icons.visibility_off : Icons.visibility),
        onPressed: toggle,
      ),
    ),
    validator: (value) {
      if (value?.isEmpty ?? true) return 'Введите пароль';
      if (isConfirm && value != _passwordController.text)
        return 'Пароли не совпадают';
      return null;
    },
  );

  Widget _buildDatePickerField() => TextFormField(
    controller: _birthDateController,
    readOnly: true,
    decoration: AppTheme.inputDecoration.copyWith(
      labelText: 'Дата рождения',
      prefixIcon: const Icon(
        Icons.calendar_month,
        color: AppColors.darkBrownIcon,
      ),
    ),
    onTap: () async {
      final picked = await showAppDatePicker(
        context: context,
        initialDate: DateTime(2010),
        firstDate: DateTime(1900),
        lastDate: DateTime.now(),
      );
      if (picked != null) {
        setState(() {
          _birthDate = picked;
          _birthDateController.text = DateFormat('dd.MM.yyyy').format(picked);
        });
      }
    },
  );

  Widget _buildRegisterButton(bool isLoading) => ElevatedButton(
    onPressed: isLoading ? null : _register,
    child: isLoading
        ? const SizedBox(
            height: 20,
            width: 20,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              color: Colors.white,
            ),
          )
        : const Text('Зарегистрироваться', style: AppTextStyles.buttonText),
  );
}
