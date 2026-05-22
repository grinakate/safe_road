import 'dart:convert';

class ContentBlock {
  final String type;
  final String? text;
  final String? url;
  final String? caption;

  ContentBlock({
    required this.type,
    this.text,
    this.url,
    this.caption,
  });

  factory ContentBlock.fromMap(Map<String, dynamic> m) {
    return ContentBlock(
      type: (m['type'] ?? '').toString(),
      text: m['text']?.toString(),
      url: m['url']?.toString(),
      caption: m['caption']?.toString(),
    );
  }
}

class TopicContent {
  final int id;
  final String title;
  final List<ContentBlock> blocks;

  TopicContent({required this.id, required this.title, required this.blocks});

  factory TopicContent.fromJson(Map<String, dynamic> json) {
    dynamic content = json['content'];
    Map<String, dynamic> contentMap;
    if (content == null) {
      contentMap = {};
    } else if (content is String) {
      try {
        contentMap = jsonDecode(content) as Map<String, dynamic>;
      } catch (_) {
        contentMap = {};
      }
    } else if (content is Map) {
      contentMap = Map<String, dynamic>.from(content);
    } else {
      contentMap = {};
    }

    final rawBlocks = (contentMap['blocks'] as List<dynamic>?) ?? [];
    final blocks = rawBlocks
        .whereType<Map<String, dynamic>>()
        .map((m) => ContentBlock.fromMap(m))
        .toList();

    return TopicContent(
      id: json['id'] is int ? json['id'] as int : int.parse(json['id'].toString()),
      title: json['title']?.toString() ?? '',
      blocks: blocks,
    );
  }
}

