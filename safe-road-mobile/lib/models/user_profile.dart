import 'package:safe_road/models/level_model.dart';

import 'achievement.dart';

class UserProfile {
  final String name;
  final LevelModel level;
  final int currentXp;
  final int completedLessons;
  final int totalLessons;
  final List<Achievement> achievements;
  final int avatarId;

  UserProfile({
    required this.name,
    required this.level,
    required this.currentXp,
    required this.completedLessons,
    required this.totalLessons,
    required this.achievements,
    required this.avatarId,
  });

  int get xpToNextLevel => level.xpThreshold - currentXp;

  double get xpProgress => currentXp / xpToNextLevel;

  factory UserProfile.fromJson(Map<String, dynamic> json) => UserProfile(
    name: json['name'],
    level: LevelModel.fromJson(json['level']),
    currentXp: json['currentXp'],
    completedLessons: json['completedLessons'],
    totalLessons: json['totalLessons'],
    achievements: (json['achievements'] as List)
        .map((a) => Achievement.fromJson(a))
        .toList(),
    avatarId: json['avatarId'],
  );

  UserProfile copyWith({
    String? name,
    LevelModel? level,
    int? currentXp,
    int? completedLessons,
    int? totalLessons,
    List<Achievement>? achievements,
    int? avatarId,
  }) {
    return UserProfile(
      name: name ?? this.name,
      level: level ?? this.level,
      currentXp: currentXp ?? this.currentXp,
      completedLessons: completedLessons ?? this.completedLessons,
      totalLessons: totalLessons ?? this.totalLessons,
      achievements: achievements ?? this.achievements,
      avatarId: avatarId ?? this.avatarId,
    );
  }
}
