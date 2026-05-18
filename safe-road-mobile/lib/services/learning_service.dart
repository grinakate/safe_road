import 'dart:convert';

import '../models/question.dart';
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

  Future<List<Question>> fetchQuizData(int topicId) async {
    final response = await _apiClient.get('/learning/test/${topicId}/quiz');

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => Question.fromJson(json)).toList();
    } else {
      throw Exception(
        'Failed to load quiz data. Status code: ${response.statusCode}',
      );
    }
  }

  Future<FinalQuizResult> submitAllAnswers(QuizFullSubmission request) async {
    final response = await _apiClient.post('/profile/quiz/submit', request);

    if (response.statusCode == 200) {
      return FinalQuizResult.fromJson(jsonDecode(response.body));
    } else {
      throw Exception(
        'Failed to submit quiz. Status code: ${response.statusCode}',
      );
    }
  }
}
