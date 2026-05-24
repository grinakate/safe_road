import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';

import '../../../data/models/learning/question_error.dart';
import '../../../logic/providers/learning_provider.dart';
import '../../../logic/providers/learning_state.dart';
import '../../theme/app_theme.dart';

class ResultScreen extends StatelessWidget {
  const ResultScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // Точечно слушаем только результат последнего теста
    final result = context.select<LearningProvider, TestResult?>(
      (p) => p.state.lastTestResult,
    );

    // Защита: если экран открыли по ошибке, а результатов нет
    if (result == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Результаты')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text('Результаты теста не найдены'),
              const SizedBox(height: 12),
              ElevatedButton(
                onPressed: () => context.go('/main'),
                child: const Text('На главную'),
              ),
            ],
          ),
        ),
      );
    }

    final correct = result.correctAnswers;
    final total = result.totalQuestions;
    final errors = result.errors;
    final int wrong = (total - correct).clamp(0, total);
    final double percent = total > 0 ? (correct / total) * 100.0 : 0.0;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Результаты теста'),
        automaticallyImplyLeading: false,
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // 1. Карточка с общим счетом и прогресс-баром
              _buildScoreCard(correct, wrong, percent),
              const SizedBox(height: 12),

              // 2. Список разбора ошибок
              if (errors.isNotEmpty) ...[
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 4.0),
                  child: Text(
                    'Разбор ошибок',
                    style: AppTextStyles.headlineLarge.copyWith(fontSize: 18),
                  ),
                ),
                const SizedBox(height: 8),
                Expanded(child: _buildErrorsList(errors)),
              ] else ...[
                const Spacer(),
              ],

              // 3. Панель кнопок действий
              _buildActionButtons(context, result.topicId, errors.isNotEmpty),
            ],
          ),
        ),
      ),
    );
  }

  // --- Вспомогательные UI методы ---
  Widget _buildScoreCard(int correct, int wrong, double percent) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Image.asset(
              wrong > 0
                  ? 'assets/images/test_result_600.png'
                  : 'assets/images/congrats_600.png',
              width: 150,
              height: 150,
              errorBuilder: (_, _, _) => const SizedBox(height: 10),
            ),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text(
                  'Правильно: $correct',
                  style: AppTextStyles.titleLarge.copyWith(
                    fontSize: 18,
                    color: AppColors.primaryGreen,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  'Ошибки: $wrong',
                  style: AppTextStyles.titleLarge.copyWith(
                    fontSize: 18,
                    color: AppColors.errorRed,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: LinearProgressIndicator(
                minHeight: 12,
                value: percent / 100.0,
                backgroundColor: AppColors.errorRed.withValues(alpha: 0.2),
                valueColor: const AlwaysStoppedAnimation<Color>(
                  AppColors.primaryGreen,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildErrorsList(List<dynamic> errors) {
    return ListView.builder(
      itemCount: errors.length,
      itemBuilder: (context, index) {
        final item = errors[index];
        final parsed = QuestionError.tryParse(item);

        String questionText = parsed?.questionText ?? 'Вопрос ${index + 1}';
        String userAnswer = parsed?.userAnswer ?? '';
        String correctAnswer = parsed?.correctAnswer ?? '';
        String analysis = parsed?.analysis ?? '';

        return Card(
          margin: const EdgeInsets.symmetric(vertical: 6),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          child: Padding(
            padding: const EdgeInsets.all(12.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Вопрос ${index + 1}: $questionText',
                  style: AppTextStyles.bodyLarge.copyWith(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 8),
                _buildAnswerRow(
                  'Вы выбрали:',
                  userAnswer,
                  AppColors.errorRed,
                  Icons.cancel,
                ),
                const SizedBox(height: 4),
                _buildAnswerRow(
                  'Верный ответ:',
                  correctAnswer,
                  AppColors.primaryGreen,
                  Icons.check_circle,
                ),
                if (analysis.isNotEmpty) ...[
                  const Divider(height: 16),
                  Text(
                    'Разбор: $analysis',
                    style: AppTextStyles.bodySmall.copyWith(
                      color: AppColors.darkBrownText,
                    ),
                  ),
                ],
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildAnswerRow(
    String label,
    String value,
    Color color,
    IconData icon,
  ) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('$label ', style: const TextStyle(fontWeight: FontWeight.w500)),
        Expanded(
          child: Text(
            value,
            style: TextStyle(color: color, fontWeight: FontWeight.bold),
          ),
        ),
        const SizedBox(width: 4),
        Icon(icon, color: color, size: 16),
      ],
    );
  }

  Widget _buildActionButtons(
    BuildContext context,
    int? topicId,
    bool hasErrors,
  ) {
    final bool isTheoryAvailable = topicId != null && topicId != 0;

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 12.0),
      child: Row(
        children: [
          if (hasErrors && isTheoryAvailable) ...[
            Expanded(
              child: ElevatedButton(
                onPressed: () {
                  context.push('/topic/$topicId');
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.white,
                  foregroundColor: AppColors.primaryGreen,
                  side: const BorderSide(
                    color: AppColors.primaryGreen,
                    width: 2,
                  ),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  padding: const EdgeInsets.symmetric(vertical: 14),
                ),
                child: Text(
                  'К теории',
                  style: AppTextStyles.buttonText.copyWith(
                    color: AppColors.primaryGreen,
                  ),
                ),
              ),
            ),
            const SizedBox(width: 12),
          ],
          Expanded(
            child: ElevatedButton(
              onPressed: () {
                context.read<LearningProvider>().clearLastResult();
                context.go('/main');
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primaryGreen,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                padding: const EdgeInsets.symmetric(vertical: 14),
              ),
              child: const Text('Продолжить', style: AppTextStyles.buttonText),
            ),
          ),
        ],
      ),
    );
  }
}
