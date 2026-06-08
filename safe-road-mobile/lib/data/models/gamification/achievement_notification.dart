class AchievementNotification {
  final int achievementId;
  final String title;
  final String iconUrl;
  final int earnedXp;

  AchievementNotification({
    required this.achievementId,
    required this.title,
    required this.iconUrl,
    required this.earnedXp,
  });

  factory AchievementNotification.fromJson(Map<String, dynamic> json) {
    return AchievementNotification(
      achievementId: json['achievementId'],
      title: json['title'],
      iconUrl: json['iconUrl'],
      earnedXp: json['earnedXp'] ?? 0,
    );
  }
}