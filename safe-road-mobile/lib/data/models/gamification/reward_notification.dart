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