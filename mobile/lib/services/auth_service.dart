import 'dart:convert';

import 'package:http/http.dart' as http;

import '../core/constants.dart';
import '../models/auth_models.dart';
import 'token_storage_service.dart';

class AuthService {
  final TokenStorageService _tokenStorageService;

  AuthService(this._tokenStorageService);

  Future<String?> login(String email, String password) async {
    final response = await http.post(
      Uri.parse('${AppConstants.baseUrl}/api/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(LoginRequest(email: email, password: password).toJson()),
    );

    if (response.statusCode == 200) {
      final tokenData = TokenResponse.fromJson(jsonDecode(response.body));
      await _tokenStorageService.saveToken(tokenData.token);
      return tokenData.token;
    }
    return null;
  }

  Future<String?> register(UserRegisterRequest request) async {
    final response = await http.post(
      Uri.parse('${AppConstants.baseUrl}/api/auth/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
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
