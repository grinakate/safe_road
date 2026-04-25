// lib/services/auth_service.dart
import 'dart:ffi';

import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:safe_road/models/user_model.dart'; // Для @visibleForTesting
import 'package:shared_preferences/shared_preferences.dart';
import 'package:intl/intl.dart';

class AuthService {
  // Замените на URL вашего бэкенда
  static const String _baseUrl =
      'http://192.168.0.5:8080/api/auth'; // Или ваш продакшн URL
  static const String _jwtTokenKey = 'jwt_token';

  // Метод для регистрации
  Future<UserModel> register({
    required String name,
    required String email,
    required String password,
    required DateTime birthDate,
  }) async {
    try {
      print("Uri: ${Uri.parse('$_baseUrl/register')}");

      final response = await http.post(
        Uri.parse('$_baseUrl/register'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'name': name,
          'email': email,
          'birthDate': DateFormat('yyyy-MM-dd').format(birthDate),
          'password': password,
          'avatarId': 1,
        }),
      );

      if (response.statusCode == 200) {
        // 201 Created обычно возвращается при успешной регистрации
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        // Если бэкенд возвращает данные пользователя после регистрации:
        return UserModel.fromJson(responseData);
      } else {
        // Попробуем получить сообщение об ошибке от бэкенда
        final errorBody = jsonDecode(response.body);
        String errorMessage = errorBody['message'] ?? 'Registration failed';
        throw Exception(errorMessage);
      }
    } catch (e) {
      print('Error during registration: $e');
      throw Exception('Could not register user. Please try again later.');
    }
  }

  // Метод для получения данных пользователя (например, после логина или для отображения профиля)
  // Пока не используется напрямую для регистрации, но понадобится для других частей приложения.
  Future<UserModel> getUserProfile(String userId) async {
    // Реализуйте запрос к вашему API для получения данных пользователя по ID
    // await http.get(Uri.parse('$_baseUrl/users/$userId'));
    throw UnimplementedError('getUserProfile not implemented');
  }

  // Сохранение токена
  Future<void> saveJwtToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_jwtTokenKey, token);
  }

  // Получение токена
  Future<String?> getJwtToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_jwtTokenKey);
  }

  // Удаление токена (при выходе из системы)
  Future<void> deleteJwtToken() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_jwtTokenKey);
  }

  // Метод для логина
  Future<void> login({required String email, required String password}) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/login'), // Ваш эндпоинт для логина
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'password': password}),
      );

      if (response.statusCode == 200) {
        // 200 OK для успешного логина
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        final String? token = responseData['token'];
        if (token == null) {
          throw Exception('Login response missing token');
        }
        // Сохраняем токен
        await saveJwtToken(token);
      } else if (response.statusCode == 401) {
        // Unauthorized
        throw Exception('Invalid email or password');
      } else {
        final errorBody = jsonDecode(response.body);
        String errorMessage = errorBody['message'] ?? 'Login failed';
        throw Exception(errorMessage);
      }
    } catch (e) {
      print('Error during login: $e');
      // Если это уже Exception, не оборачиваем его
      if (e is Exception) {
        throw e;
      }
      throw Exception('Could not log in. Please check your credentials.');
    }
  }

  // --- Метод выхода из системы ---
  Future<void> logout() async {
    await deleteJwtToken();
  }

  // --- Метод для добавления токена в заголовки запросов ---
  // Этот метод будет использоваться другими сервисами (не AuthService)
  // или может быть частью AuthService, если он управляет всеми HTTP-запросами.
  Future<Map<String, String>> _getAuthHeaders() async {
    final token = await getJwtToken();
    if (token == null) {
      // Пользователь не авторизован, обработайте это (например, перенаправьте на логин)
      // Пока просто вернем пустой Map, но в реальном приложении нужно более явное управление.
      return {'Content-Type': 'application/json'};
    }
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token', // Стандартный формат для JWT
    };
  }

  // Метод для проверки, авторизован ли пользователь
  Future<bool> isLoggedIn() async {
    final token = await getJwtToken();
    return token != null;
  }
}
