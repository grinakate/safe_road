import 'package:safe_road/data/models/learning/topic_statistics.dart';

class SectionStat {
  final int sectionId;
  final String title;
  final int completedTopics;
  final int totalTopics;
  final List<TopicStat> topicStatistics;

  SectionStat({
    required this.sectionId,
    required this.title,
    required this.completedTopics,
    required this.totalTopics,
    required this.topicStatistics,
  });

  factory SectionStat.fromJson(Map<String, dynamic> json) {
    return SectionStat(
      sectionId: json['sectionId'],
      title: json['title'],
      completedTopics: json['completedTopics'],
      totalTopics: json['totalTopics'],
      topicStatistics: (json['topicStatistics'] as List)
          .map((i) => TopicStat.fromJson(i))
          .toList(),
    );
  }
}
