import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../theme.dart';
import '../providers/learning_provider.dart';

class ResultScreen extends StatelessWidget {
  const ResultScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final lp = context.watch<LearningProvider>();
    final correct = lp.lastCorrectAnswers ?? 0;
    final total = lp.lastTotalQuestions ?? 0;
    final errors = lp.lastErrors;

    final int wrong = (total - correct).clamp(0, total);
    final double percent = total > 0 ? (correct / total) * 100.0 : 0.0;

    return Scaffold(
      appBar: AppBar(title: const Text('Результаты теста')),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Верхняя карточка с аватаром, счетом
              Card(
                elevation: 2,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                child: Padding(
                  padding: const EdgeInsets.symmetric(vertical: 18.0, horizontal: 12.0),
                  child: Column(
                    children: [
                      // аватар
                      Image.asset('assets/images/congratulations.png', width: 90, height: 90, errorBuilder: (c, e, s) => const SizedBox()),
                      const SizedBox(height: 12),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          Column(
                            children: [
                              Text('Правильно:', style: AppTextStyles.bodyLarge.copyWith(fontSize: 18, color: AppColors.primaryGreen)),
                              const SizedBox(height: 6),
                              Text('$correct', style: AppTextStyles.headlineLarge.copyWith(fontSize: 28, color: AppColors.primaryGreen)),
                            ],
                          ),
                          // прогресс
                          Expanded(
                            child: Column(
                              children: [
                                Text('Успех: ${percent.toStringAsFixed(0)}%', style: AppTextStyles.bodyLarge.copyWith(fontSize: 16)),
                                const SizedBox(height: 8),
                                ClipRRect(
                                  borderRadius: BorderRadius.circular(8),
                                  child: LinearProgressIndicator(
                                    minHeight: 24,
                                    value: percent / 100.0,
                                    backgroundColor: AppColors.lightGreenBackground,
                                    valueColor: const AlwaysStoppedAnimation<Color>(AppColors.primaryGreen),
                                  ),
                                ),
                              ],
                            ),
                          ),
                          Column(
                            children: [
                              Text('Ошибки:', style: AppTextStyles.bodyLarge.copyWith(fontSize: 18, color: AppColors.errorRed)),
                              const SizedBox(height: 6),
                              Text('$wrong', style: AppTextStyles.headlineLarge.copyWith(fontSize: 28, color: AppColors.errorRed)),
                            ],
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),

              const SizedBox(height: 12),
              Text('Разбор ошибок', style: AppTextStyles.bodyLarge),
              const SizedBox(height: 8),

              // Список разборов
              Expanded(
                child: errors.isEmpty
                    ? Center(child: Text('Ошибок нет. Отличная работа!', style: AppTextStyles.bodyLarge))
                    : ListView.builder(
                        itemCount: errors.length,
                        itemBuilder: (context, index) {
                          // backend DTO: TestErrorResponse(questionText, userAnswerText, correctAnswerText, feedback)
                          final item = errors[index];
                          String questionText = '';
                          String userAnswer = '';
                          String correctAnswer = '';
                          String analysis = '';

                          if (item is Map<String, dynamic>) {
                            questionText = (item['questionText'] ?? '').toString();
                            userAnswer = (item['userAnswerText'] ?? '').toString();
                            correctAnswer = (item['correctAnswerText'] ?? '').toString();
                            analysis = (item['feedback'] ?? '').toString();
                          } else if (item != null) {
                            // если пришёл JSON-подобный объект, пробуем toString-safe поля
                            try {
                              final m = Map<String, dynamic>.from(item as Map);
                              questionText = (m['questionText'] ?? '').toString();
                              userAnswer = (m['userAnswerText'] ?? '').toString();
                              correctAnswer = (m['correctAnswerText'] ?? '').toString();
                              analysis = (m['feedback'] ?? '').toString();
                            } catch (_) {
                              // last resort: строковое представление
                              questionText = item.toString();
                            }
                          }
                          if (questionText.isEmpty) questionText = 'Вопрос ${index + 1}';

                          return Card(
                            margin: const EdgeInsets.symmetric(vertical: 8),
                            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                            child: Padding(
                              padding: const EdgeInsets.all(12.0),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text('Вопрос ${index + 1}: $questionText', style: AppTextStyles.bodyLarge.copyWith(fontWeight: FontWeight.w600)),
                                  const SizedBox(height: 8),
                                  Row(
                                    children: [
                                      const Text('Вы выбрали: '),
                                      Expanded(
                                        child: Text('[${userAnswer}]', style: AppTextStyles.bodyLarge.copyWith(color: AppColors.errorRed)),
                                      ),
                                      if (userAnswer.isNotEmpty)
                                        const SizedBox(width: 6),
                                      if (userAnswer.isNotEmpty)
                                        Icon(Icons.cancel, color: AppColors.errorRed, size: 18),
                                    ],
                                  ),
                                  const SizedBox(height: 6),
                                  Row(
                                    children: [
                                      const Text('Правильный ответ: '),
                                      Expanded(child: Text('[${correctAnswer}]', style: AppTextStyles.bodyLarge.copyWith(color: AppColors.primaryGreen))),
                                    ],
                                  ),
                                  const SizedBox(height: 8),
                                  if (analysis.isNotEmpty)
                                    Text('- Разбор: $analysis', style: AppTextStyles.bodySmall.copyWith(color: AppColors.darkBrownText)),
                                ],
                              ),
                            ),
                          );
                        },
                      ),
              ),

              Padding(
                padding: const EdgeInsets.only(top: 12.0),
                child: Row(
                  children: [
                    Expanded(
                      child: ElevatedButton(
                        onPressed: () => Navigator.popUntil(context, (route) => route.isFirst),
                        child: const Text('Вернуться на карту'),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: OutlinedButton(
                        onPressed: () => Navigator.pop(context),
                        child: const Text('Повторить тест'),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
