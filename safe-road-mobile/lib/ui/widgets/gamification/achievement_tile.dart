import 'package:flutter/material.dart';
import 'package:safe_road/ui/widgets/common/secure_network_image.dart';

import '../../../data/models/gamification/achievement.dart';
import '../../theme/app_theme.dart';

class AchievementTile extends StatelessWidget {
  final Achievement achievement;
  final double iconSize;

  const AchievementTile({
    super.key,
    required this.achievement,
    this.iconSize = 70.0,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: iconSize,
          height: iconSize,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: AppColors.blueBackground,
            boxShadow: [
              if (achievement.isUnlocked)
                BoxShadow(
                  color: AppColors.orangeCatAccent.withValues(alpha: 0.3),
                  blurRadius: 8,
                  spreadRadius: 2,
                ),
            ],
          ),
          child: ClipOval(
            // Обрезаем содержимое по кругу
            child: ColorFiltered(
              // Если достижение открыто, фильтр не применяется (прозрачный)
              colorFilter: achievement.isUnlocked
                  ? const ColorFilter.mode(
                      Colors.transparent,
                      BlendMode.srcOver,
                    )
                  : const ColorFilter.matrix(
                      // Матрица для эффекта Grayscale
                      [
                        0.2126, 0.7152, 0.0722, 0, 0, // Red
                        0.2126, 0.7152, 0.0722, 0, 0, // Green
                        0.2126, 0.7152, 0.0722, 0, 0, // Blue
                        0, 0, 0, 1, 0, // Alpha
                      ],
                    ),
              child: SecureNetworkImage(
                imageUrl: achievement.iconUrl,
                width: iconSize,
                height: iconSize,
                fit: BoxFit.cover,
              ),
            ),
          ),
        ),
        const SizedBox(height: 8),
        Text(
          achievement.title,
          textAlign: TextAlign.center,
          style: AppTextStyles.bodySmall.copyWith(
            fontWeight: achievement.isUnlocked
                ? FontWeight.bold
                : FontWeight.normal,
          ),
        ),
      ],
    );
  }
}
