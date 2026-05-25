import 'package:flutter/material.dart';
import 'package:safe_road/data/models/learning/section_statistics.dart';
import 'package:safe_road/data/services/learning_service.dart';

import 'learning_state.dart';

class LearningProvider extends ChangeNotifier {
  final LearningService _service;

  LearningState _state = LearningState();

  LearningState get state => _state;

  List<SectionStat> _sectionStats = [];

  List<SectionStat> get sectionStats => _sectionStats;
  bool _loadingSectionStat = false;

  LearningProvider(this._service);

  bool get loadingSectionStat => _loadingSectionStat;

  /// Загрузка карты курса (Roadmap) с умным кэшированием и обработкой ошибок
  Future<void> loadRoadMap({bool forceRefresh = false}) async {
    if (_state.sections.isNotEmpty && !forceRefresh) {
      return;
    }

    _state = _state.copyWith(
      status: LearningStatus.loading,
      errorMessage: null,
    );
    notifyListeners();

    try {
      final sections = await _service.getRoadMap();
      _state = _state.copyWith(
        sections: sections,
        status: LearningStatus.success,
      );
    } catch (e) {
      _state = _state.copyWith(
        status: LearningStatus.error,
        errorMessage: e.toString(),
      );
    } finally {
      notifyListeners();
    }
  }

  /// Запись результатов пройденного теста в состояние
  void setLastResult({
    required int correct,
    required int total,
    required int experience,
    List<dynamic>? errors,
    int? topicId,
  }) {
    final result = TestResult(
      correctAnswers: correct,
      totalQuestions: total,
      totalExperience: experience,
      errors: errors ?? [],
      topicId: topicId,
    );

    _state = _state.copyWith(lastTestResult: result);
    notifyListeners();
  }

  /// Очистка результатов (полезно при выходе из экрана результатов обратно на карту)
  void clearLastResult() {
    _state = _state.copyWith(lastTestResult: null);
    notifyListeners();
  }

  Future<void> submitQuiz({
    required String sessionId,
    required Map<int, int> answers,
  }) async {
    _state = _state.copyWith(status: LearningStatus.loading);
    notifyListeners();

    try {
      final resp = await _service.submitSessionAnswers(sessionId, answers);

      setLastResult(
        correct: resp.correctCount,
        total: resp.totalQuestions,
        experience: 0,
        errors: resp.errors,
        topicId: _state.lastTestResult?.topicId,
      );

      _state = _state.copyWith(status: LearningStatus.success);
    } catch (e) {
      _state = _state.copyWith(
        status: LearningStatus.error,
        errorMessage: e.toString(),
      );
      rethrow;
    } finally {
      notifyListeners();
    }
  }

  Future<void> loadSectionStatistics({bool forceRefresh = false}) async {
    if (_loadingSectionStat && !forceRefresh) return;
    _loadingSectionStat = true;
    notifyListeners();
    try {
      _sectionStats = await _service.getSectionStatistics();
    } catch (e) {
      print('Error loading section statistics: $e');
      _sectionStats = [];
    } finally {
      _loadingSectionStat = false;
      notifyListeners();
    }
  }

  void clear() {
    _sectionStats = [];
    _state = LearningState();
    _loadingSectionStat = false;
    notifyListeners();
  }
}
