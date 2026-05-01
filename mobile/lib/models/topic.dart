import 'topic_status.dart';

class Topic {
  final int id;
  final String name;
  final int orderIndex;
  final TopicStatus status;

  Topic({
    required this.id,
    required this.name,
    required this.orderIndex,
    required this.status,
  });

  factory Topic.fromJson(Map<String, dynamic> json) {
    return Topic(
      id: json['id'],
      name: json['name'],
      orderIndex: json['orderIndex'],
      status: TopicStatus.fromString(json['status']),
    );
  }
}

