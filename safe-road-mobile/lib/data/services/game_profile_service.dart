import 'package:safe_road/data/models/gamification/avatar.dart';

import '../models/gamification/achievement.dart';
import '../models/gamification/game_profile.dart';
import 'api/api_client_v1.dart';

class GameProfileService {
  final ApiClientV1 _apiClient;

  GameProfileService(this._apiClient);

  Future<GameProfile> getProfile() async {
    final response = await _apiClient.get('/gamification/me');
    return GameProfile.fromJson(response.data);
  }

  Future<List<Achievement>> getAchievements() async {
    final response = await _apiClient.get('/gamification/me/achievements');
    return (response.data as List)
        .map((a) => Achievement.fromJson(a as Map<String, dynamic>))
        .toList();
  }

  Future<Avatar> updateAvatar(int newAvatarId) async {
    final request = ChangeAvatarRequest(avatarId: newAvatarId);
    final response = await _apiClient.post(
      '/gamification/me/avatar',
      request.toJson(),
    );
    return Avatar.fromJson(response.data);
  }

  Future<void> updateProfile(Map<String, dynamic> data) async {
    await _apiClient.patch('/gamification/me/settings', data);
    return;
  }

  Future<List<Avatar>> getAvailableAvatars() async {
    final response = await _apiClient.get('/gamification/avatars');
    return (response.data as List)
        .map((a) => Avatar.fromJson(a as Map<String, dynamic>))
        .toList();
  }
}
