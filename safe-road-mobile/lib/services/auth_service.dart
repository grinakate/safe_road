import 'dart:convert';

import '../models/auth_models.dart';
import 'api_client.dart';
import 'token_storage_service.dart';

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
      return token;
    }
    return null;
  }

  Future<void> logout() async {
    await _tokenStorageService.deleteToken();
  }

  Future<bool> isAuthenticated() async {
    return (await _tokenStorageService.getToken()) != null;
  }
}
