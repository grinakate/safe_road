class Achievement {
  final int id;
  final String title;
  final String description;
  final String iconUrl;
  final bool isUnlocked;
  final int rewardXp;

  Achievement({
    required this.id,
    required this.title,
    required this.description,
    required this.iconUrl,
    required this.isUnlocked,
    required this.rewardXp,
  });

  factory Achievement.fromJson(Map<String, dynamic> json) => Achievement(
    id: json['title'],
    title: json['title'],
    description: json['description'],
    iconUrl: json['iconUrl'],
    isUnlocked: json['unlocked'],
    rewardXp: json['rewardXp'],
  );

  Achievement copyWith({
    int? id,
    String? title,
    String? description,
    String? iconUrl,
    bool? isUnlocked,
    int? rewardXp,
  }) {
    return Achievement(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      iconUrl: iconUrl ?? this.iconUrl,
      isUnlocked: isUnlocked ?? this.isUnlocked,
      rewardXp: rewardXp ?? this.rewardXp,
    );
  }
}
