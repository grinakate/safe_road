import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../providers/learning_provider.dart';
import '../theme.dart';
import '../models/question_error.dart';
import '../providers/topic_provider.dart';
import 'topic_screen.dart';

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
          padding: const EdgeInsets.symmetric(horizontal: 12.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Верхняя карточка с аватаром, счетом
              Card(
                elevation: 2,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Padding(
                  padding: const EdgeInsets.only(
                    bottom: 8,
                    left: 20,
                    right: 20,
                  ),
                  child: Column(
                    children: [
                      // аватар
                      Image.asset(
                        'assets/images/test_result_600.png',
                        width: 180,
                        height: 180,
                        errorBuilder: (c, e, s) => const SizedBox(),
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          Text(
                            'Правильно: $correct',
                            style: AppTextStyles.titleLarge.copyWith(
                              fontSize: 18,
                              color: AppColors.primaryGreen,
                            ),
                          ),
                          Text(
                            'Ошибки: $wrong',
                            style: AppTextStyles.titleLarge.copyWith(
                              fontSize: 18,
                              color: AppColors.errorRed,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 6),
                      ClipRRect(
                        borderRadius: BorderRadius.circular(8),
                        child: LinearProgressIndicator(
                          minHeight: 16,
                          value: percent / 100.0,
                          backgroundColor: AppColors.errorRed,
                          valueColor: const AlwaysStoppedAnimation<Color>(
                            AppColors.primaryGreen,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),

              if (errors.isNotEmpty) ...[
                const SizedBox(height: 6),
                Text(
                  'Разбор ошибок',
                  style: AppTextStyles.headlineLarge.copyWith(fontSize: 18),
                ),
                const SizedBox(height: 6),
                Expanded(
                  child: ListView.builder(
                    itemCount: errors.length,
                    itemBuilder: (context, index) {
                      final item = errors[index];
                      final parsed = QuestionError.tryParse(item);

                      String questionText = parsed?.questionText ?? '';
                      String userAnswer = parsed?.userAnswer ?? '';
                      String correctAnswer = parsed?.correctAnswer ?? '';
                      String analysis = parsed?.analysis ?? '';

                      if (questionText.isEmpty) {
                        questionText = 'Вопрос ${index + 1}';
                      }

                      return Card(
                        margin: const EdgeInsets.symmetric(vertical: 8),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.symmetric(
                            vertical: 8.0,
                            horizontal: 12,
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Вопрос ${index + 1}: $questionText',
                                style: AppTextStyles.bodyLarge.copyWith(
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                              const SizedBox(height: 8),
                              Row(
                                children: [
                                  const Text('Вы выбрали: '),
                                  Expanded(
                                    child: Text(
                                      '[ $userAnswer ]',
                                      style: AppTextStyles.bodyLarge
                                          .copyWith(
                                            color: AppColors.errorRed,
                                          ),
                                    ),
                                  ),
                                  if (userAnswer.isNotEmpty)
                                    const SizedBox(width: 6),
                                  if (userAnswer.isNotEmpty)
                                    Icon(
                                      Icons.cancel,
                                      color: AppColors.errorRed,
                                      size: 18,
                                    ),
                                ],
                              ),
                              const SizedBox(height: 6),
                              Row(
                                children: [
                                  const Text('Верный ответ: '),
                                  Expanded(
                                    child: Text(
                                      '[ $correctAnswer ]',
                                      style: AppTextStyles.bodyLarge
                                          .copyWith(
                                            color: AppColors.primaryGreen,
                                          ),
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 8),
                              if (analysis.isNotEmpty)
                                Text(
                                  'Разбор: $analysis',
                                  style: AppTextStyles.bodySmall.copyWith(
                                    color: AppColors.darkBrownText,
                                  ),
                                ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
              ] else
                const Spacer(),

              Padding(
                padding: const EdgeInsets.all(10.0),
                child: Row(
                  children: [
                    if (errors.isNotEmpty) ...[
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () async {
                            final lp = context.read<LearningProvider>();
                            final topicId = lp.lastTestTopicId;
                            if (topicId == null || topicId <= 0) {
                              ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Теория недоступна для этого теста')));
                              return;
                            }

                            final topicProvider = context.read<TopicProvider>();
                            final cached = topicProvider.getCached(topicId);
                            if (cached != null) {
                              Navigator.push(
                                context,
                                MaterialPageRoute(builder: (c) => TopicScreen(topic: cached)),
                              );
                              return;
                            }

                            // показать загрузку
                            showDialog<void>(
                              context: context,
                              barrierDismissible: false,
                              builder: (_) => const Center(child: CircularProgressIndicator()),
                            );
                            try {
                              final topicContent = await topicProvider.getTopic(topicId);
                              Navigator.of(context).pop(); // remove loading
                              if (!context.mounted) return;
                              Navigator.push(
                                context,
                                MaterialPageRoute(builder: (c) => TopicScreen(topic: topicContent)),
                              );
                            } catch (e) {
                              Navigator.of(context).pop();
                              if (context.mounted) {
                                ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Не удалось загрузить теорию: ${e.toString()}')));
                              }
                            }
                          },
                          style: ElevatedButton.styleFrom(
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(16),
                            ),
                          ),
                          child: const Text(
                            'К теории',
                            style: AppTextStyles.buttonText,
                          ),
                        ),
                      ),
                      const SizedBox(width: 10),
                    ],
                    Expanded(
                      child: ElevatedButton(
                        onPressed: () => Navigator.popUntil(
                          context,
                          (route) => route.isFirst,
                        ),
                        style: ElevatedButton.styleFrom(
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(16),
                          ),
                        ),
                        child: const Text(
                          'Продолжить',
                          style: AppTextStyles.buttonText,
                        ),
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
