import 'package:flutter/material.dart';
import 'package:safe_road/widgets/secure_network_image.dart';

import '../models/achievement.dart';
import '../theme.dart';

class AchievementTile extends StatelessWidget {
  final Achievement achievement;

  const AchievementTile({super.key, required this.achievement});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          width: 10,
          height: 50,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: AppColors.errorRed.withAlpha(26),
            boxShadow: [
              if (achievement.isUnlocked)
                BoxShadow(
                  color: AppColors.orangeCatAccent.withValues(alpha: 0.3),
                  blurRadius: 8,
                  spreadRadius: 2,
                ),
            ],
          ),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: ColorFiltered(
              // Если не открыто — делаем картинку черно-белой (grayscale)
              colorFilter: achievement.isUnlocked
                  ? ColorFilter.mode(
                      AppColors.white.withAlpha(0),
                      BlendMode.dst,
                    )
                  : const ColorFilter.mode(
                      AppColors.darkBrownIcon,
                      BlendMode.saturation,
                    ),
              child: SecureNetworkImage(imageUrl: achievement.iconUrl),
            ),
          ),
        ),
        const SizedBox(height: 4),
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
