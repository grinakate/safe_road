

class GameProfile {
  final String name;
  final int level;
  final int currentXp;
  final int completedLessons;
  final int totalLessons;
  final int avatarId;
  final int currentStreak;
  final double xpProgress;

  GameProfile({
    required this.name,
    required this.level,
    required this.currentXp,
    required this.completedLessons,
    required this.totalLessons,
    required this.avatarId,
    required this.currentStreak,
    required this.xpProgress,
  });

  factory GameProfile.fromJson(Map<String, dynamic> json) => GameProfile(
    name: json['name'],
    level: json['level'],
    currentXp: json['currentXp'],
    completedLessons: json['completedLessons'] ?? 0,
    totalLessons: json['totalLessons'] ?? 100,
    avatarId: json['avatarId'],
    currentStreak: json['currentStreak'],
    xpProgress: json['xpProgress'],
  );

  GameProfile copyWith({
    String? name,
    int? level,
    int? currentXp,
    int? completedLessons,
    int? totalLessons,
    int? avatarId,
    int? currentStreak,
    double? xpProgress,
  }) {
    return GameProfile(
      name: name ?? this.name,
      level: level ?? this.level,
      currentXp: currentXp ?? this.currentXp,
      completedLessons: completedLessons ?? this.completedLessons,
      totalLessons: totalLessons ?? this.totalLessons,
      avatarId: avatarId ?? this.avatarId,
      currentStreak: currentStreak ?? this.currentStreak,
      xpProgress: xpProgress ?? this.xpProgress,
    );
  }
}
