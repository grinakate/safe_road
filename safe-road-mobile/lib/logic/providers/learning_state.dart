import '../../data/models/learning/section.dart';

enum LearningStatus { idle, loading, success, error }

/// Класс, объединяющий все данные результата теста
class TestResult {
  final int correctAnswers;
  final int totalQuestions;
  final int totalExperience;
  final List<dynamic> errors;
  final int? topicId;

  TestResult({
    required this.correctAnswers,
    required this.totalQuestions,
    required this.totalExperience,
    this.errors = const [],
    this.topicId,
  });
}

/// Единое состояние экрана обучения
class LearningState {
  final List<Section> sections;
  final LearningStatus status;
  final String? errorMessage;
  final TestResult? lastTestResult;

  LearningState({
    this.sections = const [],
    this.status = LearningStatus.idle,
    this.errorMessage,
    this.lastTestResult,
  });

  LearningState copyWith({
    List<Section>? sections,
    LearningStatus? status,
    String? errorMessage,
    TestResult? lastTestResult,
  }) {
    return LearningState(
      sections: sections ?? this.sections,
      status: status ?? this.status,
      errorMessage: errorMessage ?? this.errorMessage,
      lastTestResult: lastTestResult ?? this.lastTestResult,
    );
  }
}
