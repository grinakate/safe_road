class LevelModel {
  final int number;
  final String title;
  final int xpThreshold;

  LevelModel({
    required this.number,
    required this.title,
    required this.xpThreshold,
  });

  factory LevelModel.fromJson(Map<String, dynamic> json) {
    return LevelModel(
      number: json['number'],
      title: json['title'],
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
