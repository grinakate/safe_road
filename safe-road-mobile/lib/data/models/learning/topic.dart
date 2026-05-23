import 'topic_status.dart';

class Topic {
  final int id;
  final String title;
  final int orderIndex;
  final TopicStatus status;

  Topic({
    required this.id,
    required this.title,
    required this.orderIndex,
    required this.status,
  });

  factory Topic.fromJson(Map<String, dynamic> json) {
    return Topic(
      id: json['id'],
      title: json['title'],
      orderIndex: json['orderIndex'],
      status: TopicStatus.fromString(json['status']),
    );
  }
}

