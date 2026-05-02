class Achievement {
  final String title;
  final String description;
  final String iconUrl;
  final bool isUnlocked;

  Achievement({
    required this.title,
    required this.description,
    required this.iconUrl,
    required this.isUnlocked,
  });

  factory Achievement.fromJson(Map<String, dynamic> json) => Achievement(
    title: json['title'],
    description: json['description'],
    iconUrl: json['iconUrl'],
    isUnlocked: json['isUnlocked'],
  );
}
