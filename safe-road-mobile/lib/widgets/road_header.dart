import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../providers/game_profile_provider.dart';
import '../theme.dart';

class RoadHeader extends StatelessWidget {
  const RoadHeader({super.key});

  @override
  Widget build(BuildContext context) {
    final gp = context.watch<GameProfileProvider>();
    final profile = gp.profile;
    if (profile == null) {
      return Container(
        color: AppColors.lightBlueBackground,
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
        child: const Center(child: CircularProgressIndicator()),
      );
    }
    return Container(
      color: AppColors.lightBlueBackground,
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
      child: Column(
        children: [
          Row(
            children: [
              // TODO: добавить логику avatarId
              CircleAvatar(
                radius: 30,
                backgroundColor: AppColors.lightBlueBackground,
                child: Image.asset("assets/images/samokat.png"),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    ClipRRect(
                      borderRadius: BorderRadius.circular(10),
                      child: LinearProgressIndicator(
                        value: profile.xpProgress,
                        minHeight: 16,
                        color: AppColors.primaryGreen,
                        backgroundColor: AppColors.lightGreenBackground,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Уровень ${profile.level}: ${profile.currentXp} очков',
                style: AppTextStyles.bodyLarge.copyWith(fontWeight: FontWeight.bold, fontSize: 14),
              ),
              Text(
                '${profile.completedLessons}/${profile.totalLessons} Уроков пройдено',
                style: AppTextStyles.bodyMedium.copyWith(fontSize: 13),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
