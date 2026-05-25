import 'package:flutter/material.dart';
import 'package:safe_road/data/services/auth_service.dart';
import 'package:safe_road/logic/providers/topic_provider.dart';

import '../../core/service_locator.dart';
import '../../data/models/auth/auth_models.dart';
import '../../data/services/notification_service.dart';
import 'auth_state.dart';
import 'game_profile_provider.dart';
import 'leaderboard_provider.dart';
import 'learning_provider.dart';
import 'notification_provider.dart';

class AuthProvider extends ChangeNotifier {
  final AuthService _authService;

  // Единое состояние авторизации
  AuthState _state = AuthState.initial();

  AuthState get state => _state;

  // Удобные геттеры для UI
  bool get isAuthenticated => _state.status == AuthStatus.authenticated;

  bool get isLoading => _state.status == AuthStatus.loading;

  String? get token => _state.token;

  String? get errorMessage => _state.errorMessage;

  AuthProvider(this._authService) {
    _init();
  }

  /// Автоматическая инициализация при старте приложения
  Future<void> _init() async {
    try {
      final isAuth = await _authService.isAuthenticated();
      if (isAuth) {
        _state = AuthState(status: AuthStatus.authenticated);
        _startNotifications();
      } else {
        _state = AuthState(status: AuthStatus.unauthenticated);
      }
    } catch (e) {
      _state = AuthState(
        status: AuthStatus.unauthenticated,
        errorMessage: e.toString(),
      );
    } finally {
      notifyListeners();
    }
  }

  /// Проверить статус авторизации (например, принудительно)
  Future<bool> loadAuthState() async {
    try {
      final isAuth = await _authService.isAuthenticated();
      _state = _state.copyWith(
        status: isAuth ? AuthStatus.authenticated : AuthStatus.unauthenticated,
      );
      if (isAuth) _startNotifications();
      return isAuth;
    } catch (e) {
      _state = _state.copyWith(status: AuthStatus.unauthenticated);
      return false;
    } finally {
      notifyListeners();
    }
  }

  /// Вход в систему
  Future<String?> login(String email, String password) async {
    _state = _state.copyWith(status: AuthStatus.loading, errorMessage: null);
    notifyListeners();

    try {
      final token = await _authService.login(email, password);
      if (token != null) {
        _state = AuthState(status: AuthStatus.authenticated, token: token);
        _startNotifications();
        return token;
      } else {
        _state = AuthState(
          status: AuthStatus.error,
          errorMessage: 'Неверные учетные данные',
        );
        return null;
      }
    } catch (e) {
      _state = AuthState(
        status: AuthStatus.error,
        errorMessage: _cleanErrorMessage(e),
      );
      rethrow; // Пробрасываем ошибку дальше в UI, если нужно обработать локально
    } finally {
      notifyListeners();
    }
  }

  /// Регистрация нового пользователя
  Future<String?> register({
    required String nickname,
    required String email,
    required DateTime birthDate,
    required String password,
  }) async {
    _state = _state.copyWith(status: AuthStatus.loading, errorMessage: null);
    notifyListeners();

    final req = UserRegisterRequest(
      nickname: nickname,
      email: email,
      birthDate: birthDate,
      password: password,
    );

    try {
      final token = await _authService.register(req);
      if (token != null) {
        _state = AuthState(status: AuthStatus.authenticated, token: token);
        _startNotifications();
        return token;
      } else {
        _state = AuthState(
          status: AuthStatus.error,
          errorMessage: 'Ошибка при регистрации',
        );
        return null;
      }
    } catch (e) {
      _state = AuthState(
        status: AuthStatus.error,
        errorMessage: _cleanErrorMessage(e),
      );
      rethrow;
    } finally {
      notifyListeners();
    }
  }

  /// Выход из системы
  Future<void> logout() async {
    _state = _state.copyWith(status: AuthStatus.loading);
    notifyListeners();

    getIt<GameProfileProvider>().clear();
    getIt<LearningProvider>().clear();
    getIt<LeaderboardProvider>().clear();
    getIt<TopicProvider>().clear();
    getIt<NotificationProvider>().clear();

    try {
      await _authService.logout();
    } catch (_) {
      // Игнорируем сетевые ошибки при логауте, пользователя всё равно нужно разлогинить
    } finally {
      _state = AuthState(status: AuthStatus.unauthenticated);
      _stopNotifications();
      notifyListeners();
    }
  }

  // --- Вспомогательные методы инфраструктуры ---
  void _startNotifications() {
    try {
      getIt<NotificationService>().start();
    } catch (e) {
      debugPrint('Ошибка запуска уведомлений: $e');
    }
  }

  void _stopNotifications() {
    try {
      // И здесь тоже обращаемся к сервису
      getIt<NotificationService>().dispose();
    } catch (e) {
      debugPrint('Ошибка остановки уведомлений: $e');
    }
  }

  /// Очистка текста ошибок от технических деталей сервера (Exception: ...)
  String _cleanErrorMessage(dynamic error) {
    final rawMessage = error.toString();
    return rawMessage.replaceAll('Exception: ', '');
  }
}
