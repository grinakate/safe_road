import 'dart:convert';

import '../models/question.dart';
import 'api_client.dart';

class QuizService {
  final ApiClient _apiClient;

  QuizService(this._apiClient);

  Future<List<Question>> fetchQuizData(int topicId) async {
    final response = await _apiClient.get('/api/v1/learning/topics/${topicId}/quiz');

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
    final response = await _apiClient.post('/api/v1/profile/quiz/submit', request);

    if (response.statusCode == 200) {
      return FinalQuizResult.fromJson(jsonDecode(response.body));
    } else {
      throw Exception(
        'Failed to submit quiz. Status code: ${response.statusCode}',
      );
    }
  }
}
