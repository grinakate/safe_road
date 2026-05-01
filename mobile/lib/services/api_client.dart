import 'dart:convert';
import 'package:http/http.dart' as http;
import '../core/constants.dart';
import 'token_storage_service.dart';

class ApiClient {
  final TokenStorageService _tokenStorageService;

  ApiClient(this._tokenStorageService);

  Future<Map<String, String>> _getHeaders({bool requireAuth = true}) async {
    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
    if (requireAuth) {
      String? token = await _tokenStorageService.getToken();
      if (token != null) {
        headers['Authorization'] = 'Bearer $token';
      }
    }
    return headers;
  }

  Future<http.Response> get(String endpoint, {bool requireAuth = true}) async {
    final url = Uri.parse('${AppConstants.baseUrl}$endpoint');
    final response = await http.get(url, headers: await _getHeaders(requireAuth: requireAuth));
    _handleResponseErrors(response);
    return response;
  }

  Future<http.Response> post(String endpoint, dynamic body, {bool requireAuth = true}) async {
    final url = Uri.parse('${AppConstants.baseUrl}$endpoint');
    final response = await http.post(
      url,
      headers: await _getHeaders(requireAuth: requireAuth),
      body: jsonEncode(body),
    );
    _handleResponseErrors(response);
    return response;
  }

  void _handleResponseErrors(http.Response response) {
    if (response.statusCode == 401) {
      throw Exception('Unauthorized');
    }
  }
}
