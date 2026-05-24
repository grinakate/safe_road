import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:safe_road/data/models/gamification/achievement.dart';
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

  /// Обработчик входящих уведомлений о награде (SSE)
  void handleRewardEvent(String jsonString) {
    try {
      final payload = jsonDecode(jsonString) as Map<String, dynamic>;
      final reward = RewardNotification.fromJson(payload);

      updateXpAndLevel(reward.totalXp, reward.newLevel);
    } catch (e) {
      debugPrint('Ошибка при обработке награды: $e');
    }
  }

  /// Обновление профиля при получении награды.
  void updateXpAndLevel(int totalXp, int newLevel) {
    if (_profile == null) return;

    bool hasChanges = false;

    int updatedXp = _profile!.currentXp;
    if (totalXp > _profile!.currentXp) {
      updatedXp = totalXp;
      hasChanges = true;
    }

    int updatedLevel = _profile!.level;
    if (newLevel > _profile!.level) {
      updatedLevel = newLevel;
      hasChanges = true;
    }

    if (!hasChanges) {
      debugPrint(
        'Уведомление проигнорировано: пришедший XP ($totalXp) <= текущего (${_profile!.currentXp})',
      );
      return;
    }

    _profile = _profile!.copyWith(currentXp: updatedXp, level: updatedLevel);

    notifyListeners();
  }
}
