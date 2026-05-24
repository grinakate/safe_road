class TopicStat {
  final int topicId;
  final String title;
  final int totalQuestions;
  final int correctAnswers;
  final int wrongAnswers;
  final int notShown;

  TopicStat({
    required this.topicId,
    required this.title,
    required this.totalQuestions,
    required this.correctAnswers,
    required this.wrongAnswers,
    required this.notShown,
  });

  factory TopicStat.fromJson(Map<String, dynamic> json) {
    return TopicStat(
      topicId: json['topicId'],
      title: json['title'],
      totalQuestions: json['totalQuestions'],
      correctAnswers: json['correctAnswers'],
      wrongAnswers: json['wrongAnswers'],
      notShown: json['notShown'],
    );
  }
}
