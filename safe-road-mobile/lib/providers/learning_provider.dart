import 'package:flutter/material.dart';
import 'package:safe_road/models/section.dart';
import 'package:safe_road/services/learning_service.dart';
import '../core/service_locator.dart';

class LearningProvider extends ChangeNotifier {
  final LearningService _service = getIt<LearningService>();

  List<Section> _sections = [];
  Map<String, dynamic> _progress = {};
  int? _lastCorrectAnswers;
  int? _lastTotalQuestions;
  int? _lastTotalExperience;
  List<dynamic> _lastErrors = [];

  int? get lastCorrectAnswers => _lastCorrectAnswers;
  int? get lastTotalQuestions => _lastTotalQuestions;
  int? get lastTotalExperience => _lastTotalExperience;
  List<dynamic> get lastErrors => _lastErrors;

  List<Section> get sections => _sections;
  Map<String, dynamic> get progress => _progress;

  Future<void> loadSections() async {
    _sections = await _service.getRoadMap();
    notifyListeners();
  }

  Future<void> loadProgress() async {
    // placeholder for progress; backend method not implemented in example
    notifyListeners();
  }

  void setLastResult(int correct, int total, int experience, {List<dynamic>? errors}) {
    _lastCorrectAnswers = correct;
    _lastTotalQuestions = total;
    _lastTotalExperience = experience;
    _lastErrors = errors ?? [];
    notifyListeners();
  }
}


