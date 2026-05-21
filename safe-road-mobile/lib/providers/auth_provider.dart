import 'package:flutter/material.dart';
import 'package:safe_road/services/auth_service.dart';
import '../core/service_locator.dart';
import '../models/auth_models.dart';
import 'notification_provider.dart';

class AuthProvider extends ChangeNotifier {
  final AuthService _authService = getIt<AuthService>();

  AuthProvider() {
    _init();
  }

  void _init() async {
    // Проверяем состояние авторизации при старте провайдера и запускаем Notifications, если уже авторизованы
    try {
      final ok = await _authService.isAuthenticated();
      _isAuthenticated = ok;
      if (ok) {
        try {
          getIt<NotificationProvider>().start();
        } catch (_) {}
      }
      notifyListeners();
    } catch (_) {}
  }

  String? _token;
  bool _isAuthenticated = false;

  String? get token => _token;
  bool get isAuthenticated => _isAuthenticated;

  Future<bool> loadAuthState() async {
    final ok = await _authService.isAuthenticated();
    _isAuthenticated = ok;
    notifyListeners();
    return ok;
  }

  Future<String?> login(String email, String password) async {
    final token = await _authService.login(email, password);
    if (token != null) {
      _token = token;
      _isAuthenticated = true;
      notifyListeners();
      try {
        getIt<NotificationProvider>().start();
      } catch (_) {}
    }
    return token;
  }

  Future<String?> register(String nickname, String email, DateTime birthDate, String password) async {
    final req = UserRegisterRequest(nickname: nickname, email: email, birthDate: birthDate, password: password);
    final token = await _authService.register(req);
    if (token != null) {
      _token = token;
      _isAuthenticated = true;
      notifyListeners();
      try {
        getIt<NotificationProvider>().start();
      } catch (_) {}
    }
    return token;
  }

  Future<void> logout() async {
    await _authService.logout();
    _token = null;
    _isAuthenticated = false;
    try {
      getIt<NotificationProvider>().stop();
    } catch (_) {}
    notifyListeners();
  }
}



