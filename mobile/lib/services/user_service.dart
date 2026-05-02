import 'dart:convert';
import 'api_client.dart';
import '../models/user_profile.dart';

class UserService {
  final ApiClient _apiClient;
  UserService(this._apiClient);

  Future<UserProfile> getProfile() async {
    final response = await _apiClient.get('/profile/me');
    if (response.statusCode == 200) {
      return UserProfile.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    }
    throw Exception('Failed to load profile');
  }
}
