import 'package:safe_road/data/models/learning/section_statistics.dart';

import '../models/learning/question.dart';
import '../models/learning/section.dart';
import 'api/api_client_v1.dart';

class LearningService {
  final ApiClientV1 _apiClient;

  LearningService(this._apiClient);

  /// Получить дорожную карту обучения
  Future<List<Section>> getRoadMap() async {
    final response = await _apiClient.get('/learning/roadmap');
    return (response.data as List)
        .map((json) => Section.fromJson(json))
        .toList();
  }

  /// Начать новую сессию теста для темы
  Future<StartTestResponse> startTest(int topicId) async {
    final body = {'topicId': topicId, 'mode': 'QUIZ'};
    final response = await _apiClient.post('/learning/test/start', body);
    return StartTestResponse.fromJson(response.data);
  }

  /// Начать финальный тест для целого раздела
  Future<StartTestResponse> startSectionTest(int sectionId) async {
    final body = {'sectionId': sectionId, 'mode': 'FINAL'};
    final response = await _apiClient.post('/learning/test/start', body);
    return StartTestResponse.fromJson(response.data);
  }

  /// Получить вопросы для активной сессии теста
  Future<List<Question>> getTestQuestions(String sessionId) async {
    final response = await _apiClient.get(
      '/learning/test/$sessionId/questions',
    );
    return (response.data as List)
        .map((json) => Question.fromJson(json))
        .toList();
  }

  /// Отправить все ответы сессии за один раз
  Future<TestSessionSubmitResult> submitSessionAnswers(
    String sessionId,
    Map<int, int> answers,
  ) async {
    final body = {
      'answers': answers.map((key, value) => MapEntry(key.toString(), value)),
    };

    final response = await _apiClient.post(
      '/learning/test/$sessionId/submit-answer',
      body,
    );
    return TestSessionSubmitResult.fromJson(response.data);
  }

  /// Получение статистики ответов на вопросы по разделам и топикам
  Future<List<SectionStat>> getSectionStatistics() async {
    final response = await _apiClient.get('/learning/me/statistics');
    return (response.data as List)
        .map((e) => SectionStat.fromJson(e))
        .toList();
  }
}
