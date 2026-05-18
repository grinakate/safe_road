class Question {
  final int id;
  final String text;
  final List<AnswerOption> options;
  final int correctAnswerId;

  Question({
    required this.id,
    required this.text,
    required this.options,
    required this.correctAnswerId,
  });

  factory Question.fromJson(Map<String, dynamic> json) {
    var optionsList = json['options'] as List;
    List<AnswerOption> options = optionsList
        .map((i) => AnswerOption.fromJson(i))
        .toList();
    return Question(
      id: json['id'],
      text: json['text'],
      options: options,
      correctAnswerId: json['correctAnswerId'],
    );
  }
}

class AnswerOption {
  final int id;
  final String text;
  final String feedback;
  final bool isCorrect;

  AnswerOption({
    required this.id,
    required this.text,
    required this.feedback,
    required this.isCorrect,
  });

  factory AnswerOption.fromJson(Map<String, dynamic> json) {
    return AnswerOption(
      id: json['id'],
      text: json['text'],
      feedback: json['feedBack'],
      isCorrect: json['isCorrect'],
    );
  }
}

// Отдельный ответ пользователя
class UserAnswer {
  final int questionId;
  final int selectedAnswerId;

  UserAnswer({required this.questionId, required this.selectedAnswerId});

  factory UserAnswer.fromJson(Map<String, dynamic> json) {
    return UserAnswer(
      questionId: json['questionId'],
      selectedAnswerId: json['selectedAnswerId'],
    );
  }

  Map<String, dynamic> toJson() {
    return {'questionId': questionId, 'selectedAnswerId': selectedAnswerId};
  }
}

class QuizFullSubmission {
  final List<UserAnswer> answers;

  QuizFullSubmission({required this.answers});

  Map<String, dynamic> toJson() {
    return {'answers': answers.map((answer) => answer.toJson()).toList()};
  }
}

class FinalQuizResult {
  final int correctAnswers;
  final int totalQuestions;
  final int awardedExperience;
  final bool newLevel;

  FinalQuizResult({
    required this.correctAnswers,
    required this.totalQuestions,
    required this.awardedExperience,
    required this.newLevel,
  });

  factory FinalQuizResult.fromJson(Map<String, dynamic> json) {
    return FinalQuizResult(
      correctAnswers: json['correctAnswers'] ?? 0,
      totalQuestions: json['totalQuestions'] ?? 0,
      awardedExperience: json['awardedExperience'] ?? 0,
      newLevel: json['newLevel'] ?? false,
    );
  }
}
