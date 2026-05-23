import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/ui/widgets/common/secure_network_image.dart';

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
      color: AppColors.lightBlueBackground,
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
      child: Column(
        children: [
          Row(
            children: [
              _buildAvatar(profile.avatar.url),
              const SizedBox(width: 12),
              Expanded(child: _buildProgressBar(profile.xpProgress)),
            ],
          ),
          const SizedBox(height: 12),
          _buildInfoRow(profile),
        ],
      ),
    );
  }

  Widget _buildAvatar(String avatarUrl) {
    return CircleAvatar(
      radius: 30,
      backgroundColor: AppColors.white,
      child: avatarUrl.isNotEmpty
          ? ClipOval(
              child: SecureNetworkImage(
                imageUrl: avatarUrl,
                fit: BoxFit.cover,
                width: 60,
                height: 60,
              ),
            )
          : Image.asset("assets/images/samokat.png"),
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

  Widget _buildInfoRow(dynamic profile) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          'Уровень ${profile.level}: ${profile.currentXp} XP',
          style: AppTextStyles.bodyLarge.copyWith(
            fontWeight: FontWeight.bold,
            fontSize: 14,
          ),
        ),
        Text(
          '${profile.completedLessons}/${profile.totalLessons} уроков',
          style: AppTextStyles.bodyMedium.copyWith(fontSize: 13),
        ),
      ],
    );
  }
}
