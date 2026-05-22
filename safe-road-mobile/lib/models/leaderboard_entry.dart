class LeaderboardEntry {
  final int userId;
  final int xp;
  final int rank;
  final bool isCurrentUser;
  final String nickname;
  final String avatarUrl;

  LeaderboardEntry({
    required this.userId,
    required this.xp,
    required this.rank,
    required this.isCurrentUser,
    required this.nickname,
    required this.avatarUrl,
  });

  factory LeaderboardEntry.fromJson(Map<String, dynamic> json) {
    return LeaderboardEntry(
      userId: (json['userId'] as num?)?.toInt() ?? 0,
      xp: (json['xp'] as num?)?.toInt() ?? 0,
      rank: (json['rank'] as num?)?.toInt() ?? 0,
      isCurrentUser: (json['isCurrentUser'] as bool?) ?? false,
      nickname: (json['nickname'] ?? '').toString(),
      avatarUrl: (json['avatarUrl'] ?? '').toString(),
    );
  }
}
