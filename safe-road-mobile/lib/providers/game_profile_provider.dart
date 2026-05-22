import 'package:flutter/material.dart';
import 'package:safe_road/models/game_profile.dart';
import 'package:safe_road/models/avatar_models.dart';
import 'package:safe_road/models/achievement.dart';
import 'package:safe_road/services/game_profile_service.dart';
import '../core/service_locator.dart';

class RewardNotification {
  final int earnedXp;
  final int totalXp;
  final bool levelUp;
  final int newLevel;

  RewardNotification({
    required this.earnedXp,
    required this.totalXp,
    required this.levelUp,
    required this.newLevel,
  });

  factory RewardNotification.fromJson(Map<String, dynamic> json) {
    return RewardNotification(
      earnedXp: json['earnedXp'] ?? 0,
      totalXp: json['totalXp'] ?? 0,
      levelUp: json['levelUp'] ?? false,
      newLevel: json['newLevel'] ?? 0,
    );
  }
}

class GameProfileProvider extends ChangeNotifier {
  final GameProfileService _service = getIt<GameProfileService>();

  GameProfile? _profile;
  GameProfile? get profile => _profile;

  List<AvatarModel> _availableAvatars = [];
  List<AvatarModel> get availableAvatars => _availableAvatars;
  List<Achievement> _achievements = [];
  List<Achievement> get achievements => _achievements;
  bool _loadingAchievements = false;
  bool get loadingAchievements => _loadingAchievements;

  Future<void> loadProfile() async {
    _profile = await _service.getProfile();
    notifyListeners();
  }

  Future<void> loadAvatars() async {
    _availableAvatars = await _service.getAvailableAvatars();
    notifyListeners();
  }

  Future<void> loadAchievements() async {
    // Prefer cached list if available; otherwise fetch and cache.
    if (_achievements.isNotEmpty) return;
    _loadingAchievements = true;
    notifyListeners();
    try {
      _achievements = await _service.getAchievements();
    } catch (_) {
      _achievements = [];
    }
    _loadingAchievements = false;
    notifyListeners();
  }

  /// Force refresh achievements from network (background or explicit)
  Future<void> refreshAchievements() async {
    _loadingAchievements = true;
    notifyListeners();
    try {
      _achievements = await _service.getAchievements();
    } catch (_) {
      // keep old cache on error
    }
    _loadingAchievements = false;
    notifyListeners();
  }

  Future<void> updateAvatar(int avatarId) async {
    final newAvatar = await _service.updateAvatar(avatarId);
    if (_profile != null) {
      _profile = _profile!.copyWith(avatarId: newAvatar.id);
      notifyListeners();
    }
  }

  /// Обновление профиля при получении уведомления о наградах
  void updateFromNotification(RewardNotification reward) {
    if (_profile == null) return;
    _profile = _profile!.copyWith(
      currentXp: reward.totalXp,
      level: reward.newLevel > 0 ? reward.newLevel : _profile!.level,
      xpProgress: reward.totalXp.toDouble(),
    );
    notifyListeners();
  }
}



