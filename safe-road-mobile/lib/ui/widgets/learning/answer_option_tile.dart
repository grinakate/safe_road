import 'package:flutter/material.dart';
import 'package:safe_road/ui/theme/app_theme.dart';

import '../../../data/models/learning/question.dart';


class AnswerOptionTile extends StatelessWidget {
  final AnswerOption option;
  final bool isSelected;
  final bool isCorrect;
  final bool isUserAnswer;
  final VoidCallback onTap;
  final bool isEnabled;

  const AnswerOptionTile({
    super.key,
    required this.option,
    required this.isSelected,
    required this.isCorrect,
    required this.isUserAnswer,
    required this.onTap,
    this.isEnabled = true,
  });

  @override
  Widget build(BuildContext context) {
    // Определяем цвета в зависимости от состояния
    Color backgroundColor = AppColors.white;
    Color borderColor = AppColors.greyBorder;
    Color textColor = AppColors.darkBrownText;
    IconData? suffixIcon;
    Color? iconColor;

    if (isEnabled) {
      // Состояние ДО выбора ответа
      if (isSelected) {
        backgroundColor = AppColors.lightBlueBackground;
        borderColor = AppColors.primaryGreen;
        textColor = AppColors.primaryGreen;
      }
    } else {
      // Состояние после того, как ответ выбран (режим показа результатов)
      if (isCorrect) {
        // Правильный ответ (подсвечиваем всегда)
        backgroundColor = AppColors.lightGreenBackground;
        borderColor = AppColors.darkGreen;
        textColor = AppColors.darkGreen;
        suffixIcon = Icons.check_circle;
        iconColor = AppColors.darkGreen;
      } else if (isUserAnswer && !isCorrect) {
        // Ответ пользователя, если он оказался неверным
        backgroundColor = AppColors.errorRed.withValues(alpha: 0.1);
        borderColor = AppColors.errorRed;
        textColor = AppColors.errorRed;
        suffixIcon = Icons.cancel;
        iconColor = AppColors.errorRed;
      }
    }

    return GestureDetector(
      onTap: isEnabled ? onTap : null,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        margin: const EdgeInsets.symmetric(vertical: 8),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: backgroundColor,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: borderColor, width: 2),
          boxShadow: isSelected && isEnabled
              ? [
                  BoxShadow(
                    color: borderColor.withValues(alpha: 0.3),
                    blurRadius: 4,
                  ),
                ]
              : null,
        ),
        child: Row(
          children: [
            Expanded(
              child: Text(
                option.text,
                style: AppTextStyles.bodyLarge.copyWith(
                  fontSize: 16,
                  color: textColor,
                  fontWeight: isSelected || isCorrect
                      ? FontWeight.bold
                      : FontWeight.normal,
                ),
              ),
            ),
            if (suffixIcon != null) Icon(suffixIcon, color: iconColor),
          ],
        ),
      ),
    );
  }
}
