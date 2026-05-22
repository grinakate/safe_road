import 'dart:convert';

import 'api_client.dart';
import 'package:safe_road/models/topic_content.dart';

class ContentService {
  final ApiV1Client _apiClient;

  ContentService(this._apiClient);

  /// Fetch topic by id. Expects backend to return JSON like {id, title, content}
  /// where content is either a JSON object with `blocks` or a JSON string.
  Future<TopicContent> fetchTopic(int topicId) async {
    final response = await _apiClient.get('/content/topics/$topicId');

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = jsonDecode(response.body);

      return TopicContent.fromJson(data);
    } else {
      throw Exception('Failed to load topic: ${response.statusCode}');
    }
  }
}


