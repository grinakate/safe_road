import 'dart:convert';

import '../models/auth_models.dart';
import 'api_client.dart';
import 'token_storage_service.dart';
// Removed unused imports: get_it and notification_service

class AuthService {
  final TokenStorageService _tokenStorageService;
  final ApiV1Client _apiClient;

  AuthService(this._tokenStorageService, this._apiClient);

  Future<String?> login(String email, String password) async {
    final response = await _apiClient.post(
      '/auth/login',
      LoginRequest(email: email, password: password).toJson(),
      requireAuth: false,
    );

    if (response.statusCode == 200) {
      final tokenData = TokenResponse.fromJson(jsonDecode(response.body));
      await _tokenStorageService.saveToken(tokenData.token);
      // Подписка на уведомления теперь управляется через NotificationProvider
      return tokenData.token;
    }
    return null;
  }

  Future<String?> register(UserRegisterRequest request) async {
    final response = await _apiClient.post(
      '/auth/register',
      request.toJson(),
      requireAuth: false,
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      final data = jsonDecode(response.body);
      String token = data['token'];
      await _tokenStorageService.saveToken(token);
      // Подписка на уведомления теперь управляется через NotificationProvider
      return token;
    }
    return null;
  }

  Future<void> logout() async {
    await _tokenStorageService.deleteToken();
    // Уведомления теперь останавливаются через NotificationProvider
  }

  Future<bool> isAuthenticated() async {
    return (await _tokenStorageService.getToken()) != null;
  }
}
