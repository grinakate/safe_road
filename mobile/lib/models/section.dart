import 'topic.dart';

class Section {
  final int id;
  final String name;
  final int progressPercent;
  final List<Topic> topics;

  Section({
    required this.id,
    required this.name,
    required this.progressPercent,
    required this.topics,
  });

  factory Section.fromJson(Map<String, dynamic> json) {
    return Section(
      id: json['id'],
      name: json['name'],
      progressPercent: json['progressPercent'] ?? 0,
      topics: (json['topics'] as List)
          .map((topicJson) => Topic.fromJson(topicJson))
          .toList(),
    );
  }
}

