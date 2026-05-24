import 'topic.dart';

class Section {
  final int id;
  final String title;
  final String description;
  final String url;
  final int progressPercent;
  final List<Topic> topics;

  Section({
    required this.id,
    required this.title,
    required this.description,
    required this.url,
    required this.progressPercent,
    required this.topics,
  });

  factory Section.fromJson(Map<String, dynamic> json) {
    return Section(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      url: json['url'],
      progressPercent: json['progressPercent'] ?? 0,
      topics: (json['topics'] as List)
          .map((topicJson) => Topic.fromJson(topicJson))
          .toList(),
    );
  }
}

