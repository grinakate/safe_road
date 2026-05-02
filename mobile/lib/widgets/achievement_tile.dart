import 'package:flutter/material.dart';
import 'package:safe_road/widgets/secure_network_image.dart';

import '../core/constants.dart';
import '../models/achievement.dart';

class AchievementTile extends StatelessWidget {
  final Achievement achievement;

  const AchievementTile({super.key, required this.achievement});

  @override
  Widget build(BuildContext context) {
    return Opacity(
      opacity: achievement.isUnlocked ? 1.0 : 0.4,
      child: Column(
        children: [
          Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: Colors.white,
              boxShadow: [
                if (achievement.isUnlocked)
                  BoxShadow(
                    color: Colors.orange.withValues(alpha: 0.3),
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
                    ? const ColorFilter.mode(Colors.transparent, BlendMode.dst)
                    : const ColorFilter.mode(Colors.grey, BlendMode.saturation),
                child: SecureNetworkImage(
                  imageUrl: '${AppConstants.baseUrl}${achievement.iconUrl}',
                ),
              ),
            ),
          ),
          const SizedBox(height: 4),
          Text(
            achievement.title,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: 11,
              fontWeight: achievement.isUnlocked
                  ? FontWeight.bold
                  : FontWeight.normal,
            ),
          ),
        ],
      ),
    );
  }
}
