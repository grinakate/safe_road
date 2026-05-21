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
    final response = await _apiClient.get('/learning/test/$topicId/quiz');

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => Question.fromJson(json)).toList();
    } else {
      throw Exception(
        'Failed to load quiz data. Status code: ${response.statusCode}',
      );
    }
  }

  /// Start a new test session for a topic. Returns session id (string).
  Future<StartTestResponse> startTest(int topicId) async {
    final body = {'topicId': topicId, 'mode': 'QUIZ'};
    final response = await _apiClient.post('/learning/test/start', body);

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = jsonDecode(response.body);
      return StartTestResponse.fromJson(data);
    } else {
      throw Exception('Failed to start test session: ${response.statusCode}');
    }
  }

  /// Start a final test session for a whole section. Returns session id.
  Future<StartTestResponse> startSectionTest(int sectionId) async {
    final body = {'sectionId': sectionId, 'mode': 'FINAL'};
    final response = await _apiClient.post('/learning/test/start', body);

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = jsonDecode(response.body);
      return StartTestResponse.fromJson(data);
    } else {
      throw Exception('Failed to start section test session: ${response.statusCode}');
    }
  }

  /// Get questions for the test session
  Future<List<Question>> getTestQuestions(String sessionId) async {
    final response = await _apiClient.get(
      '/learning/test/$sessionId/questions',
    );

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      // Map backend TestQuestionResponse -> client Question
      return data.map<Question>((json) {
        return Question.fromJson(json);
      }).toList();
    } else {
      throw Exception(
        'Failed to load session questions: ${response.statusCode}',
      );
    }
  }

  /// Submit single answer for the active session; returns server feedback
  Future<SubmitAnswerResult> submitSessionAnswer(
    String sessionId,
    int questionId,
    int selectedAnswerId,
  ) async {
    // Backend expects SubmitAnswerRequest { answerId }
    final body = {'answerId': selectedAnswerId};
    final response = await _apiClient.post(
      '/learning/test/session/$sessionId/answer',
      body,
    );

    if (response.statusCode == 200) {
      return SubmitAnswerResult.fromJson(jsonDecode(response.body));
    } else {
      throw Exception(
        'Failed to submit session answer: ${response.statusCode}',
      );
    }
  }

  /// Submit all answers for the session in one call. Backend expects map questionId->answerId
  Future<TestSessionSubmitResult> submitSessionAnswers(
    String sessionId,
    Map<int, int> answers,
  ) async {
    // Convert keys to strings to ensure JSON maps serialize properly
    final body = {
      'answers': Map.fromEntries(
        answers.entries.map((e) => MapEntry(e.key.toString(), e.value)),
      ),
    };
    // However backend expects the raw map, not nested under 'answers', so send body as the map itself
    final response = await _apiClient.post(
      '/learning/test/$sessionId/submit-answer',
      body,
    );

    if (response.statusCode == 200) {
      return TestSessionSubmitResult.fromJson(jsonDecode(response.body));
    } else {
      throw Exception(
        'Failed to submit session answers: ${response.statusCode}',
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
