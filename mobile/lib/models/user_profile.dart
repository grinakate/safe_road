import 'package:safe_road/models/level_model.dart';

import 'achievement.dart';

class UserProfile {
  final String name;
  final LevelModel level;
  final int currentXp;
  final int completedLessons;
  final int totalLessons;
  final List<Achievement> achievements;

  UserProfile({
    required this.name,
    required this.level,
    required this.currentXp,
    required this.completedLessons,
    required this.totalLessons,
    required this.achievements,
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
  );
}
