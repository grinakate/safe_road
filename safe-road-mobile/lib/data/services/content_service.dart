import '../models/learning/topic_content.dart';
import 'api/api_client_v1.dart';

class ContentService {
  final ApiClientV1 _apiClient;

  ContentService(this._apiClient);

  Future<TopicContent> fetchTopic(int topicId) async {
    final response = await _apiClient.get('/content/topics/$topicId');
    return TopicContent.fromJson(response.data);
  }
}
