import 'package:flutter/foundation.dart';

/// Модель для представления ошибки вопроса (разбор ошибки).
class QuestionError {
  final String questionText;
  final String userAnswer;
  final String correctAnswer;
  final String analysis;

  const QuestionError({
    required this.questionText,
    required this.userAnswer,
    required this.correctAnswer,
    required this.analysis,
  });

  /// Создаёт экземпляр из Map<String, dynamic>.
  factory QuestionError.fromMap(Map<String, dynamic> map) {
    return QuestionError(
      questionText: (map['questionText'] ?? '').toString(),
      userAnswer: (map['userAnswerText'] ?? '').toString(),
      correctAnswer: (map['correctAnswerText'] ?? '').toString(),
      analysis: (map['feedback'] ?? '').toString(),
    );
  }

  /// Пытается распарсить произвольный объект в QuestionError.
  ///
  /// Метод не использует dynamic в сигнатуре — принимает [Object?] и
  /// аккуратно пробует привести к Map<String, dynamic>.
  /// Возвращает null, если item == null.
  static QuestionError? tryParse(Object? item) {
    if (item == null) return null;

    if (item is Map<String, dynamic>) {
      return QuestionError.fromMap(item);
    }

    // Если это обычная Map (не типизированная), пробуем скопировать её в
    // Map<String, dynamic> безопасно.
    if (item is Map) {
      try {
        return QuestionError.fromMap(Map<String, dynamic>.from(item));
      } catch (_) {
        // Не удалось привести — fallback к строковому представлению
        return QuestionError(
          questionText: item.toString(),
          userAnswer: '',
          correctAnswer: '',
          analysis: '',
        );
      }
    }

    // Для остальных типов просто используем toString() как вопрос
    return QuestionError(
      questionText: item.toString(),
      userAnswer: '',
      correctAnswer: '',
      analysis: '',
    );
  }
}

