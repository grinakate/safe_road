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
    final exp = lp.lastTotalExperience ?? 0;

    // Показываем диалог поздравления, если есть опыт
    if (exp > 0) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return AlertDialog(
              title: Text('Поздравляем!'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text('Вы заработали $exp опыта!'),
                  SizedBox(height: 20),
                  Image.asset('assets/images/congratulations.png', width: 100, height: 100, errorBuilder: (context, error, stackTrace) => Icon(Icons.image_not_supported)),
                ],
              ),
              actions: <Widget>[
                TextButton(child: Text('Отлично!'), onPressed: () => Navigator.of(context).pop()),
              ],
            );
          },
        );
      });
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Результаты теста')),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('Тест завершен!', style: AppTextStyles.titleLarge),
              SizedBox(height: 20),
              Text('Вы ответили правильно на:', style: AppTextStyles.bodyLarge),
              Text('$correct из $total', style: AppTextStyles.headlineLarge.copyWith(fontSize: 36, color: AppColors.primaryGreen)),
              SizedBox(height: 20),
              Text('Общий заработанный опыт:', style: AppTextStyles.bodyLarge),
              Text('$exp', style: AppTextStyles.headlineLarge.copyWith(fontSize: 36, color: AppColors.orangeCatAccent)),
              SizedBox(height: 40),
              ElevatedButton(onPressed: () => Navigator.popUntil(context, (route) => route.isFirst), child: const Text('Вернуться на карту')),
            ],
          ),
        ),
      ),
    );
  }
}
