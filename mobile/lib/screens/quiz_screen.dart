import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:safe_road/screens/result_screen.dart';
import 'package:safe_road/services/quiz_service.dart';
import '../theme.dart';

import '../models/question.dart';

class QuizScreen extends StatefulWidget {
  final List<Question> quizData;

  const QuizScreen({super.key, required this.quizData});

  @override
  _QuizScreenState createState() => _QuizScreenState();
}

class _QuizScreenState extends State<QuizScreen> {
  int _currentQuestionIndex = 0;
  int? _selectedAnswerId; // ID выбранного ответа на текущий вопрос
  bool _showResultArea =
      false; // Показываем область с результатом и кнопкой "Далее"
  bool _isLoading = false; // Общий флаг загрузки (для финальной отправки)

  // Список для хранения данных каждого ответа пользователя
  final List<UserAnswer> _userAnswers = [];

  List<Question> get _questions => widget.quizData;

  int get _totalQuestions => _questions.length;

  Question get _currentQuestion => _questions[_currentQuestionIndex];

  void _handleAnswerSelection(int answerId) {
    if (_showResultArea)
      return; // Не даем выбрать повторно, пока не просмотрели результат

    setState(() {
      _selectedAnswerId = answerId;
      _showResultArea =
          true; // Показываем область результата сразу после выбора
    });
  }

  void _handleNextQuestion() {
    // Сохраняем ответ пользователя для текущего вопроса
    _userAnswers.add(
      UserAnswer(
        questionId: _currentQuestion.id,
        selectedAnswerId: _selectedAnswerId!,
      ),
    );

    if (_currentQuestionIndex < _totalQuestions - 1) {
      // Переход к следующему вопросу
      setState(() {
        _currentQuestionIndex++;
        _selectedAnswerId = null; // Сбрасываем выбор для нового вопроса
        _showResultArea = false; // Скрываем область результата
      });
    } else {
      // Конец теста, отправляем финальные результаты
      _sendFinalResultsAndNavigate();
    }
  }

  Future<void> _sendFinalResultsAndNavigate() async {
    if (_isLoading) return; // Предотвращаем множественные нажатия

    setState(() {
      _isLoading = true;
    });

    try {
      final submission = QuizFullSubmission(answers: _userAnswers);
      final finalResult = await GetIt.I<QuizService>().submitAllAnswers(
        submission,
      );

      if (mounted) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => ResultScreen(
              correctAnswers: finalResult.correctAnswers,
              totalQuestions: finalResult.totalQuestions,
              totalExperience: finalResult.awardedExperience, // Передаем опыт
            ),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Ошибка отправки результатов: ${e.toString()}'),
          ),
        );
        setState(() {
          _isLoading = false; // Сбрасываем флаг загрузки при ошибке
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_questions.isEmpty) {
      return Scaffold(
        appBar: AppBar(title: Text('Тест')),
        body: Center(child: Text('Нет вопросов для теста.')),
      );
    }

    final bool isCorrect =
        _selectedAnswerId != null &&
        _selectedAnswerId == _currentQuestion.correctAnswerId;
    final String resultText = _selectedAnswerId != null
        ? (isCorrect ? 'Правильно!' : 'Неправильно')
        : '';
    final feedback = "";

    return Scaffold(
      appBar: AppBar(
        title: Text('Вопрос ${_currentQuestionIndex + 1} из $_totalQuestions'),
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(4.0),
          child: LinearProgressIndicator(
            value: (_currentQuestionIndex + 1) / _totalQuestions,
            backgroundColor: AppColors.lightGreenBackground,
            valueColor: const AlwaysStoppedAnimation<Color>(AppColors.primaryGreen),
          ),
        ),
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                children: [
                  Image.asset("assets/images/think.png", height: 90),
                  Expanded(
                    child: Text(
                      _currentQuestion.text,
                        style: AppTextStyles.titleLarge.copyWith(fontSize: 20),
                    ),
                  ),
                ],
              ),
              SizedBox(height: 20),
              // Варианты ответов
              ..._currentQuestion.options.map(
                (option) => AnswerOptionWidget(
                  option: option,
                  isSelected: _selectedAnswerId == option.id,
                  // Показываем правильный/неправильный ответ только после выбора
                  isCorrect:
                      _showResultArea &&
                      option.id == _currentQuestion.correctAnswerId,
                  isUserAnswer:
                      _showResultArea && option.id == _selectedAnswerId,
                  onTap: () => _handleAnswerSelection(option.id),
                  isEnabled:
                      !_showResultArea, // Отключаем выбор после выбора первого ответа
                ),
              ),

              Spacer(),

              // Область результата и пояснения
              if (_showResultArea)
                _buildResultArea(resultText, isCorrect, feedback),

              // Кнопка "Далее" или "Завершить"
              if (_showResultArea)
                Padding(
                  padding: const EdgeInsets.only(top: 20.0),
                  child: ElevatedButton(
                    onPressed: _isLoading ? null : _handleNextQuestion,
                    // Отключаем, если идет загрузка
                    child: _isLoading
                        ? const CircularProgressIndicator(color: AppColors.white)
                        : Text(
                            _currentQuestionIndex < _totalQuestions - 1
                                ? 'Далее'
                                : 'Завершить',
                          ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildResultArea(String resultText, bool isCorrect, String feedback) {
    return Container(
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: isCorrect
            ? AppColors.lightGreenBackground
            : AppColors.errorRed.withAlpha(51),
        borderRadius: BorderRadius.circular(8),
      ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              resultText,
              style: AppTextStyles.bodyLarge.copyWith(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: isCorrect ? AppColors.primaryGreen : AppColors.errorRed,
              ),
            ),
          // Можно добавить пояснение, если оно приходит с бэка
          SizedBox(height: 16),
        ],
      ),
    );
  }
}

// AnswerOptionWidget остается прежним
class AnswerOptionWidget extends StatelessWidget {
  final AnswerOption option;
  final bool isSelected;
  final bool isCorrect;
  final bool isUserAnswer;
  final VoidCallback onTap;
  final bool isEnabled;

  const AnswerOptionWidget({
    Key? key,
    required this.option,
    required this.isSelected,
    required this.isCorrect,
    required this.isUserAnswer,
    required this.onTap,
    this.isEnabled = true,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color backgroundColor = AppColors.white;
    Color borderColor = AppColors.greyBorder;
    Color textColor = AppColors.darkBrownText;

      if (isSelected) {
        backgroundColor = AppColors.lightBlueBackground;
        borderColor = AppColors.primaryGreen;
        textColor = AppColors.primaryGreen;
      }

      if (isCorrect && isUserAnswer) {
        backgroundColor = AppColors.lightGreenBackground;
        borderColor = AppColors.primaryGreen;
        textColor = AppColors.primaryGreen;
      } else if (isUserAnswer && !isCorrect) {
        backgroundColor = AppColors.errorRed.withAlpha(26);
        borderColor = AppColors.errorRed;
        textColor = AppColors.errorRed;
      } else if (isCorrect && !isUserAnswer && !isSelected) {
        backgroundColor = AppColors.lightGreenBackground;
        borderColor = AppColors.primaryGreen;
        textColor = AppColors.primaryGreen;
      }

    return GestureDetector(
      onTap: isEnabled ? onTap : null,
      child: Container(
        margin: EdgeInsets.symmetric(vertical: 8),
        padding: EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: backgroundColor,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: borderColor, width: 1.5),
        ),
        child: Row(
          children: [
            Expanded(
              child: Text(
                option.text,
                style: AppTextStyles.bodyLarge.copyWith(fontSize: 16, color: textColor),
              ),
            ),
            if (isCorrect && !isSelected && isUserAnswer)
              Icon(Icons.check_circle, color: AppColors.primaryGreen),
            if (isUserAnswer && !isCorrect)
              Icon(Icons.cancel, color: AppColors.errorRed),
          ],
        ),
      ),
    );
  }
}
