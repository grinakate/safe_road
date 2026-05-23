import 'package:flutter/material.dart';
import 'package:safe_road/ui/widgets/common/secure_network_image.dart';

import '../../../data/models/gamification/achievement.dart';
import '../../theme/app_theme.dart';

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
      backgroundColor: Colors.transparent,
      elevation: 0,

      child: Container(
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          color: AppColors.white,
          borderRadius: BorderRadius.circular(16),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min, // Занимает минимальную высоту
          children: [
            // Иконка достижения
            Opacity(
              // Прозрачность в зависимости от статуса
              opacity: achievement.isUnlocked ? 1.0 : 0.4,
              child: SecureNetworkImage(
                imageUrl: achievement.iconUrl,
                width: imageSize,
                height: imageSize,
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
            Text(
              '+${achievement.rewardXp} опыта',
              style: AppTextStyles.achievementXp,
              textAlign: TextAlign.center,
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
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () => Navigator.of(context).pop(),
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primaryGreen, // Пример
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: Text('Закрыть', style: AppTextStyles.buttonText),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
