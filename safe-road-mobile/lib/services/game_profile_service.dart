import 'dart:convert';

import 'package:safe_road/models/achievement.dart';
import 'package:safe_road/models/avatar_models.dart';

import '../models/game_profile.dart';
import 'api_client.dart';

class GameProfileService {
  final ApiV1Client _apiClient;

  GameProfileService(this._apiClient);

  Future<GameProfile> getProfile() async {
    final response = await _apiClient.get('/gamification/me');
    if (response.statusCode == 200) {
      return GameProfile.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    }
    throw Exception('Failed to load profile');
  }

  Future<List<Achievement>> getAchievements() async {
    final response = await _apiClient.get('/gamification/me/achievements');
    if (response.statusCode == 200) {
      return (jsonDecode(utf8.decode(response.bodyBytes)) as List)
          .map((a) => Achievement.fromJson(a))
          .toList();
    }
    throw Exception('Failed to load achievements');
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
    final response = await _apiClient.get('/gamification/avatars');
    if (response.statusCode == 200) {
      return (jsonDecode(utf8.decode(response.bodyBytes)) as List)
          .map((a) => AvatarModel.fromJson(a))
          .toList();
    }
    throw Exception('Failed to load avatars');
  }
}
