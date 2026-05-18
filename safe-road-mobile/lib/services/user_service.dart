import 'dart:convert';

import 'package:safe_road/models/avatar_models.dart';

import '../models/user_profile.dart';
import 'api_client.dart';

class UserService {
  final ApiV1Client _apiClient;

  UserService(this._apiClient);

  Future<UserProfile> getProfile() async {
    final response = await _apiClient.get('/profile/me');
    if (response.statusCode == 200) {
      return UserProfile.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    }
    throw Exception('Failed to load profile');
  }

  Future<AvatarModel> updateAvatar(int newAvatarId) async {
    final request = ChangeAvatarRequest(avatarId: newAvatarId);
    final response = await _apiClient.post(
      '/profile/me/avatar',
      request.toJson(),
    );
    if (response.statusCode == 200) {
      return AvatarModel.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    }
    throw Exception('Failed to change avatar');
  }

  Future<List<AvatarModel>> getAvailableAvatars() async {
    final response = await _apiClient.get('/profile/avatars');
    if (response.statusCode == 200) {
      return (jsonDecode(utf8.decode(response.bodyBytes)) as List)
          .map((a) => AvatarModel.fromJson(a))
          .toList();
    }
    throw Exception('Failed to load avatars');
  }
}
