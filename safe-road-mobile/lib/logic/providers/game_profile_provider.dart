import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:safe_road/data/models/gamification/achievement.dart';
import 'package:safe_road/data/models/gamification/achievement_notification.dart';
import 'package:safe_road/data/models/gamification/game_profile.dart';
import 'package:safe_road/data/models/gamification/reward_notification.dart';
import 'package:safe_road/data/services/game_profile_service.dart';

class GameProfileProvider extends ChangeNotifier {
  final GameProfileService _service;

  GameProfile? _profile;

  GameProfile? get profile => _profile;

  List<Achievement> _achievements = [];

  List<Achievement> get achievements => _achievements;

  bool _loadingProfile = false;

  bool get loadingProfile => _loadingProfile;

  bool _loadingAchievements = false;

  GameProfileProvider(this._service);

  bool get loadingAchievements => _loadingAchievements;

  /// Загружает профиль
  Future<void> loadProfile() async {
    _loadingProfile = true;
    notifyListeners();

    try {
      _profile = await _service.getProfile();
    } catch (e) {
      debugPrint('Ошибка загрузки профиля: $e');
    } finally {
      _loadingProfile = false;
      notifyListeners();
    }
  }

  /// Загрузка достижений
  Future<void> loadAchievements() async {
    _loadingAchievements = true;
    notifyListeners();

    try {
      _achievements = await _service.getAchievements();
    } catch (e) {
      debugPrint('Ошибка загрузки достижений: $e');
      _achievements = []; // Очищаем список при ошибке
    } finally {
      _loadingAchievements = false;
      notifyListeners();
    }
  }

  /// Принудительное обновление достижений из сети
  Future<void> refreshAchievements() async {
    _loadingAchievements = true;
    notifyListeners();
    try {
      _achievements = await _service.getAchievements();
    } catch (e) {
      debugPrint('Ошибка обновления достижений: $e');
    } finally {
      _loadingAchievements = false;
      notifyListeners();
    }
  }

  /// Сменить аватар: отправляем запрос, и при успехе обновляем локальный профиль
  Future<void> updateAvatar(int avatarId) async {
    try {
      if (_profile?.avatar.id == avatarId) {
        return;
      }
      final updatedAvatar = await _service.updateAvatar(avatarId);
      _profile =
          _profile?.copyWith(
            avatar: UserAvatar(id: updatedAvatar.id, url: updatedAvatar.url),
          ) ??
          _profile;
      notifyListeners();
    } catch (e) {
      debugPrint('Ошибка обновления аватара: $e');
      rethrow;
    }
  }

  Future<void> updateProfile({
    required String nickname,
    String? password,
    required bool notifications,
    required bool leaderboard,
  }) async {
    if (_profile?.nickname == nickname &&
        password == null &&
        _profile?.leaderboardEnabled == leaderboard) {
      return;
    }

    // 1. Формируем тело запроса для вашего API
    final Map<String, dynamic> request = {};

    if (_profile?.nickname != nickname) {
      request['nickname'] = nickname;
    }

    if (password != null) {
      request['password'] = password;
    }

    if (_profile?.leaderboardEnabled != leaderboard) {
      request['leaderboardEnabled'] = leaderboard;
    }

    if (request.isEmpty) {
      return;
    }

    // 2. Вызываем сервис
    await _service.updateProfile(request);

    // 3. Обновляем локальный профиль, чтобы UI сразу обновился
    _profile = _profile?.copyWith(
      nickname: nickname,
      leaderboardEnabled: leaderboard,
    );
    notifyListeners();
  }

  /// Обработчик входящих уведомлений о награде (SSE)
  void handleRewardEvent(String jsonString) {
    try {
      final payload = jsonDecode(jsonString) as Map<String, dynamic>;
      final reward = RewardNotification.fromJson(payload);

      updateXp(reward.totalXp);
      updateLevel(reward.newLevel);
    } catch (e) {
      debugPrint('Ошибка при обработке награды: $e');
    }
  }

  /// Обработчик входящих уведомлений о новых достижениях (SSE)
  void handleAchievementEvent(String jsonString) {
    try {
      final payload = jsonDecode(jsonString) as Map<String, dynamic>;
      final achievement = AchievementNotification.fromJson(payload);

      updateAchievement(achievement.achievementId);
    } catch (e) {
      debugPrint('Ошибка при обработке полученного достижения: $e');
    }
  }

  /// Обновление профиля при получении награды.
  void updateXp(int totalXp) {
    if (_profile == null) return;

    int updatedXp = _profile!.currentXp;
    if (totalXp > _profile!.currentXp) {
      updatedXp = totalXp;
      _profile = _profile!.copyWith(currentXp: updatedXp);
      notifyListeners();
    } else {
      debugPrint(
        'Уведомление проигнорировано: пришедший XP ($totalXp) <= текущего (${_profile!.currentXp})',
      );
    }
  }

  /// Обновление профиля при получении награды.
  void updateLevel(int newLevel) {
    if (_profile == null) return;

    int updatedLevel = _profile!.level;
    if (newLevel > _profile!.level) {
      updatedLevel = newLevel;
      _profile = _profile!.copyWith(level: updatedLevel);
      notifyListeners();
    }
  }

  /// Обновление профиля при получении достижения.
  void updateAchievement(int achievementId) {
    if (_profile == null) return;

    final index = _achievements.indexWhere((a) => a.id == achievementId);

    if (index == -1) {
      debugPrint(
        'Ошибка: Достижение с ID $achievementId не найдено в локальном кэше.',
      );
      return;
    }

    if (_achievements[index].isUnlocked) {
      return;
    }

    final updatedAchievement = _achievements[index].copyWith(isUnlocked: true);
    _achievements[index] = updatedAchievement;
    notifyListeners();
  }

  void clear() {
    _achievements = [];
    _profile = null;
    _loadingProfile = false;
    _loadingAchievements = false;
    notifyListeners();
  }
}
