import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import '../../data/models/learning/question.dart';
import '../../data/services/learning_service.dart';
import '../../logic/providers/learning_provider.dart';
import '../../logic/providers/learning_state.dart';
import '../theme/app_theme.dart';
import '../widgets/learning/answer_option_tile.dart';

class QuizScreen extends StatefulWidget {
  final String sessionId;

  const QuizScreen({super.key, required this.sessionId});

  @override
  State<QuizScreen> createState() => _QuizScreenState();
}

class _QuizScreenState extends State<QuizScreen> {
  int _currentQuestionIndex = 0;
  int? _selectedAnswerId;
  bool _showResultArea = false;

  final List<UserAnswer> _userAnswers = [];
  List<Question> _questions = [];
  bool _isFetchingQuestions = true;

  @override
  void initState() {
    super.initState();
    _loadQuestions();
  }

  @override
  void dispose() {
    super.dispose();
  }

  Future<void> _loadQuestions() async {
    try {
      final questions = await GetIt.I<LearningService>().getTestQuestions(
        widget.sessionId,
      );
      if (mounted) {
        setState(() {
          _questions = questions;
          _isFetchingQuestions = false;
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Ошибка: $e')));
        context.pop(); // Возвращаемся назад при ошибке загрузки
      }
    }
  }

  void _handleAnswerSelection(int answerId) {
    if (_showResultArea) return;

    setState(() {
      _selectedAnswerId = answerId;
      _showResultArea = true;
      _userAnswers.add(
        UserAnswer(
          questionId: _questions[_currentQuestionIndex].id,
          selectedAnswerId: answerId,
        ),
      );
    });
  }

  void _onNextPressed() {
    if (_currentQuestionIndex < _questions.length - 1) {
      setState(() {
        _currentQuestionIndex++;
        _selectedAnswerId = null;
        _showResultArea = false;
      });
    } else {
      _finishQuiz();
    }
  }

  Future<void> _finishQuiz() async {
    final provider = context.read<LearningProvider>();
    // Преобразуем ответы в нужный формат Map
    final Map<int, int> answersMap = {
      for (var a in _userAnswers) a.questionId: a.selectedAnswerId,
    };

    try {
      await provider.submitQuiz(
        sessionId: widget.sessionId,
        answers: answersMap,
      );

      if (mounted) {
        context.go('/result'); // Переход на экран результатов
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('Ошибка отправки: $e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isFetchingQuestions) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (_questions.isEmpty) {
      return const Scaffold(body: Center(child: Text('Вопросы не найдены')));
    }

    final currentQuestion = _questions[_currentQuestionIndex];
    final learningStatus = context.select<LearningProvider, LearningStatus>(
      (p) => p.state.status,
    );
    final isSubmitting = learningStatus == LearningStatus.loading;

    return Scaffold(
      appBar: _buildAppBar(),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            children: [
              _buildQuestionHeader(currentQuestion),
              const SizedBox(height: 24),
              Expanded(
                child: ListView(
                  children: currentQuestion.options
                      .map((opt) => _buildOption(opt, currentQuestion))
                      .toList(),
                ),
              ),
              if (_showResultArea)
                _buildBottomFeedback(currentQuestion, isSubmitting),
            ],
          ),
        ),
      ),
    );
  }

  PreferredSizeWidget _buildAppBar() {
    return AppBar(
      title: Text(
        'Вопрос ${_currentQuestionIndex + 1} из ${_questions.length}',
      ),
      bottom: PreferredSize(
        preferredSize: const Size.fromHeight(4),
        child: LinearProgressIndicator(
          value: (_currentQuestionIndex + 1) / _questions.length,
          backgroundColor: AppColors.lightGreenBackground,
          color: AppColors.primaryGreen,
        ),
      ),
    );
  }

  Widget _buildQuestionHeader(Question question) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Image.asset("assets/images/think.png", height: 70),
        const SizedBox(width: 12),
        Expanded(
          child: Text(
            question.text,
            style: AppTextStyles.titleLarge.copyWith(fontSize: 18),
          ),
        ),
      ],
    );
  }

  Widget _buildOption(AnswerOption option, Question question) {
    return AnswerOptionTile(
      option: option,
      isSelected: _selectedAnswerId == option.id,
      isCorrect: _showResultArea && option.id == question.correctAnswerId,
      isUserAnswer: _showResultArea && option.id == _selectedAnswerId,
      onTap: () => _handleAnswerSelection(option.id),
      isEnabled: !_showResultArea,
    );
  }

  Widget _buildBottomFeedback(Question question, bool isSubmitting) {
    final isCorrect = _selectedAnswerId == question.correctAnswerId;
    final feedback = question.options
        .firstWhere((o) => o.id == _selectedAnswerId)
        .feedback;

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.05),
            blurRadius: 10,
          ),
        ],
        border: Border.all(color: AppColors.greyBorder),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            isCorrect ? '✨ Отлично!' : '😔 Увы, неправильно',
            style: TextStyle(
              fontSize: 18,
              fontWeight: FontWeight.bold,
              color: isCorrect ? AppColors.darkGreen : AppColors.errorRed,
            ),
          ),
          const SizedBox(height: 8),
          Text(feedback, textAlign: TextAlign.center),
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: isSubmitting ? null : _onNextPressed,
              child: isSubmitting
                  ? const CircularProgressIndicator(color: Colors.white)
                  : Text(
                      _currentQuestionIndex < _questions.length - 1
                          ? 'Далее'
                          : 'Завершить',
                    ),
            ),
          ),
        ],
      ),
    );
  }
}
