class GameProfile {
  final String nickname;
  final int level;
  final int currentXp;
  final int completedLessons;
  final int totalLessons;
  final UserAvatar avatar;
  final int currentStreak;
  final double xpProgress;
  final bool leaderboardEnabled;

  GameProfile({
    required this.nickname,
    required this.level,
    required this.currentXp,
    required this.completedLessons,
    required this.totalLessons,
    required this.avatar,
    required this.currentStreak,
    required this.xpProgress,
    required this.leaderboardEnabled,
  });

  factory GameProfile.fromJson(Map<String, dynamic> json) => GameProfile(
    nickname: json['name'],
    level: json['level'],
    currentXp: json['currentXp'],
    completedLessons: json['completedLessons'] ?? 0,
    totalLessons: json['totalLessons'] ?? 100,
    avatar: UserAvatar.fromJson(json['avatar']),
    currentStreak: json['currentStreak'],
    xpProgress: json['xpProgress'],
    leaderboardEnabled: json['leaderboardEnabled'],
  );

  GameProfile copyWith({
    String? nickname,
    int? level,
    int? currentXp,
    int? completedLessons,
    int? totalLessons,
    UserAvatar? avatar,
    int? currentStreak,
    double? xpProgress,
    bool? leaderboardEnabled,
  }) {
    return GameProfile(
      nickname: nickname ?? this.nickname,
      level: level ?? this.level,
      currentXp: currentXp ?? this.currentXp,
      completedLessons: completedLessons ?? this.completedLessons,
      totalLessons: totalLessons ?? this.totalLessons,
      avatar: avatar ?? this.avatar,
      currentStreak: currentStreak ?? this.currentStreak,
      xpProgress: xpProgress ?? this.xpProgress,
      leaderboardEnabled: leaderboardEnabled ?? this.leaderboardEnabled,
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
