class GameProfile {
  final String name;
  final int level;
  final int currentXp;
  final int completedLessons;
  final int totalLessons;
  final UserAvatar avatar;
  final int currentStreak;
  final double xpProgress;

  GameProfile({
    required this.name,
    required this.level,
    required this.currentXp,
    required this.completedLessons,
    required this.totalLessons,
    required this.avatar,
    required this.currentStreak,
    required this.xpProgress,
  });

  factory GameProfile.fromJson(Map<String, dynamic> json) => GameProfile(
    name: json['name'],
    level: json['level'],
    currentXp: json['currentXp'],
    completedLessons: json['completedLessons'] ?? 0,
    totalLessons: json['totalLessons'] ?? 100,
    avatar: UserAvatar.fromJson(json['avatar']),
    currentStreak: json['currentStreak'],
    xpProgress: json['xpProgress'],
  );

  GameProfile copyWith({
    String? name,
    int? level,
    int? currentXp,
    int? completedLessons,
    int? totalLessons,
    UserAvatar? avatar,
    int? currentStreak,
    double? xpProgress,
  }) {
    return GameProfile(
      name: name ?? this.name,
      level: level ?? this.level,
      currentXp: currentXp ?? this.currentXp,
      completedLessons: completedLessons ?? this.completedLessons,
      totalLessons: totalLessons ?? this.totalLessons,
      avatar: avatar ?? this.avatar,
      currentStreak: currentStreak ?? this.currentStreak,
      xpProgress: xpProgress ?? this.xpProgress,
    );
  }
}

class UserAvatar {
  final int id;
  final String url;

  UserAvatar({required this.id, required this.url});

  factory UserAvatar.fromJson(Map<String, dynamic> json) {
    return UserAvatar(id: json['id'], url: json['url']);
  }
}
