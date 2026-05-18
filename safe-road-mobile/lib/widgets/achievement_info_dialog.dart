import 'package:flutter/material.dart';
import 'package:safe_road/models/achievement.dart';
import 'package:safe_road/widgets/secure_network_image.dart';
import '../theme.dart';

class AchievementInfoDialog extends StatelessWidget {
  final Achievement achievement;
  final double imageSize;

  const AchievementInfoDialog({
    super.key,
    required this.achievement,
    this.imageSize = 120,
  });

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      elevation: 0,
      backgroundColor: AppColors.white.withAlpha(0),
      child: Container(
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          color: AppColors.white,
          borderRadius: BorderRadius.circular(16),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Иконка достижения
            Opacity(
              opacity: achievement.isUnlocked
                  ? 1.0
                  : 0.4,
              child: SizedBox(
                width: imageSize,
                height: imageSize,
                child: SecureNetworkImage(
                  imageUrl: achievement.iconUrl,
                  // Добавьте placeholder/errorWidget, если они нужны
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Название достижения
            Text(
              achievement.title,
              textAlign: TextAlign.center,
                style: AppTextStyles.achievementTitle,
            ),
            const SizedBox(height: 10),

            // Количество опыта
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  '+${achievement.rewardXp} опыта',
                  style: AppTextStyles.achievementXp,
                ),
              ],
            ),
            const SizedBox(height: 12),
            // Описание достижения
            Text(
              achievement.description,
              textAlign: TextAlign.center,
              style: AppTextStyles.achievementDesc,
            ),
            const SizedBox(height: 20),

            // Кнопка закрытия
            ElevatedButton(
              onPressed: () => Navigator.of(context).pop(),
              child: Text(
                'Закрыть',
                style: AppTextStyles.buttonText,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
