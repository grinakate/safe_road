import 'dart:convert';
import '../models/section.dart';
import 'api_client.dart';

class LearningService {
  final ApiV1Client _apiClient;

  LearningService(this._apiClient);

  Future<List<Section>> getRoadMap() async {
    final response = await _apiClient.get('/learning/roadmap');

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => Section.fromJson(json)).toList();
    } else {
      throw Exception('Failed to load map data: ${response.statusCode}');
    }
  }
}
