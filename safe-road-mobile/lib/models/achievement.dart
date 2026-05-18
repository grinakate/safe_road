class Achievement {
  final String title;
  final String description;
  final String iconUrl;
  final bool isUnlocked;
  final int rewardXp;

  Achievement({
    required this.title,
    required this.description,
    required this.iconUrl,
    required this.isUnlocked,
    required this.rewardXp,
  });

  factory Achievement.fromJson(Map<String, dynamic> json) => Achievement(
    title: json['title'],
    description: json['description'],
    iconUrl: json['iconUrl'],
    isUnlocked: json['isUnlocked'],
    rewardXp: json['rewardXp'],
  );
}
