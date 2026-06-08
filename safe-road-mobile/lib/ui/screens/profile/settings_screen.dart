import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../logic/providers/game_profile_provider.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final _nameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();

  bool _notificationsEnabled = true;
  bool _leaderboardEnabled = true;
  bool _isSaving = false;

  @override
  void initState() {
    super.initState();
    final profile = context.read<GameProfileProvider>().profile;
    _nameController.text = profile?.nickname ?? 'йцукен';
    _leaderboardEnabled = profile?.leaderboardEnabled ?? true;
  }

  Future<void> _saveSettings() async {
    if (_passwordController.text != _confirmPasswordController.text) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Пароли не совпадают')));
      return;
    }

    setState(() => _isSaving = true);

    try {
      await context.read<GameProfileProvider>().updateProfile(
        nickname: _nameController.text,
        password: _passwordController.text.isNotEmpty
            ? _passwordController.text
            : null,
        notifications: _notificationsEnabled,
        leaderboard: _leaderboardEnabled,
      );
      if (mounted) Navigator.pop(context);
    } catch (e) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text('Ошибка: $e')));
    } finally {
      if (mounted) setState(() => _isSaving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Настройки профиля")),
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  TextField(
                    controller: _nameController,
                    decoration: const InputDecoration(labelText: "Имя"),
                  ),
                  const SizedBox(height: 8),
                  TextField(
                    controller: _passwordController,
                    decoration: const InputDecoration(
                      labelText: "Новый пароль",
                    ),
                    obscureText: true,
                  ),
                  const SizedBox(height: 8),
                  TextField(
                    controller: _confirmPasswordController,
                    decoration: const InputDecoration(
                      labelText: "Подтверждение",
                    ),
                    obscureText: true,
                  ),
                  const SizedBox(height: 8),
                  SwitchListTile(
                    title: const Text("Получение push-уведомлений"),
                    value: _notificationsEnabled,
                    onChanged: (v) => setState(() => _notificationsEnabled = v),
                  ),
                  SwitchListTile(
                    title: const Text("Участие в лидербордах"),
                    value: _leaderboardEnabled,
                    onChanged: (v) => setState(() => _leaderboardEnabled = v),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _isSaving ? null : _saveSettings,
                  child: _isSaving
                      ? const CircularProgressIndicator()
                      : const Text("Сохранить"),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
