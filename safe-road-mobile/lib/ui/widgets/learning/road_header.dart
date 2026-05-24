import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/ui/widgets/common/secure_network_image.dart';

import '../../../data/models/gamification/game_profile.dart';
import '../../../logic/providers/game_profile_provider.dart';
import '../../theme/app_theme.dart';

class RoadHeader extends StatelessWidget {
  const RoadHeader({super.key});

  @override
  Widget build(BuildContext context) {
    final profile = context.select((GameProfileProvider gp) => gp.profile);

    if (profile == null) {
      return const SizedBox(
        height: 100,
        child: Center(child: CircularProgressIndicator()),
      );
    }

    return Container(
      margin: const EdgeInsets.all(10), // Отступы по бокам и сверху
      padding: const EdgeInsets.all(10), // Внутренний отступ для содержимого
      decoration: BoxDecoration(
        color: AppColors.white, // Цвет фона самого хедера
        borderRadius: BorderRadius.circular(20), // Закругленные углы
        boxShadow: [ // Небольшая тень, чтобы приподнять его
          BoxShadow(
            color: AppColors.darkBrownText.withOpacity(0.1),
            blurRadius: 10,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: Row(
        children: [
          _buildAvatar(profile.avatar.url),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              children: [
                _buildInfoRow(profile),
                const SizedBox(height: 4),
                _buildProgressBar(profile.xpProgress),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildAvatar(String avatarUrl) {
    return CircleAvatar(
      radius: 30,
      backgroundColor: AppColors.white,
      child: SecureNetworkImage(
        imageUrl: avatarUrl,
        fit: BoxFit.fitHeight,
        width: 60,
        height: 60,
      ),
    );
  }

  Widget _buildProgressBar(double progress) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(10),
      child: LinearProgressIndicator(
        value: progress.clamp(0.0, 1.0),
        minHeight: 16,
        color: AppColors.primaryGreen,
        backgroundColor: AppColors.lightGreenBackground,
      ),
    );
  }

  Widget _buildInfoRow(GameProfile profile) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          'Уровень ${profile.level}: ${profile.currentXp} XP',
          style: AppTextStyles.bodyLarge.copyWith(
            fontWeight: FontWeight.w500,
            fontSize: 14,
          ),
        ),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.local_fire_department,
              color: AppColors.orangeCatAccent,
              size: 24,
            ),
            // Иконка огня
            const SizedBox(width: 4),
            Text(
              profile.currentStreak.toString(),
              style: AppTextStyles.bodyMedium.copyWith(
                color: AppColors.brownText,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
      ],
    );
  }
}
