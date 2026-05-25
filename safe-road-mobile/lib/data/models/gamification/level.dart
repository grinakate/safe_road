class Level {
  final int number;
  final String title;
  final int xpThreshold;

  Level({
    required this.number,
    required this.title,
    required this.xpThreshold,
  });

  factory Level.fromJson(Map<String, dynamic> json) {
    return Level(
      number: json['number'],
      title: json['name'],
      xpThreshold: json['xpThreshold'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'number': number,
      'title': title,
      'xpThreshold': xpThreshold,
    };
  }
}
