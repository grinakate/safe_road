import '../models/auth/auth_models.dart';
import 'api/api_client_v1.dart';
import 'storage/token_storage_service.dart';

class AuthService {
  final TokenStorageService _tokenStorageService;
  final ApiClientV1 _apiClient;

  AuthService(this._tokenStorageService, this._apiClient);

  Future<String?> login(String email, String password) async {
    // Клиент сам отправит запрос и выкинет ошибку, если статус не 2xx
    final response = await _apiClient.post(
      '/auth/login',
      LoginRequest(email: email, password: password).toJson(),
      requireAuth: false,
    );

    // Dio сам распарсил JSON в response.data
    final tokenData = TokenResponse.fromJson(response.data);
    await _tokenStorageService.saveToken(tokenData.token);

    return tokenData.token;
  }

  Future<String?> register(UserRegisterRequest request) async {
    final response = await _apiClient.post(
      '/auth/register',
      request.toJson(),
      requireAuth: false,
    );

    // Достаем токен напрямую из распарсенного Map
    final String token = response.data['token'];
    await _tokenStorageService.saveToken(token);

    return token;
  }

  Future<void> logout() async {
    await _tokenStorageService.deleteToken();
  }

  Future<bool> isAuthenticated() async {
    final token = await _tokenStorageService.getToken();
    return token != null;
  }
}
