import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../logic/providers/game_profile_provider.dart';
import '../../theme/app_theme.dart';
import '../common/secure_network_image.dart';
import '../dialogs/avatar_selection_dialog.dart';

class ProfileHeader extends StatelessWidget {
  const ProfileHeader({super.key});

  @override
  Widget build(BuildContext context) {
    // Consumer теперь внутри изолированного виджета.
    // Он будет перерисовываться только при изменении GameProfileProvider.
    return Consumer<GameProfileProvider>(
      builder: (context, gp, child) {
        if (gp.profile == null) {
          return const Center(child: CircularProgressIndicator());
        }

        final user = gp.profile!;
        final userAvatar = user.avatar;

        return Container(
          margin: const EdgeInsets.only(left: 10, right: 10, bottom: 10),
          // Отступы по бокам и сверху
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 16),
          // Внутренний отступ для содержимого
          decoration: BoxDecoration(
            color: AppColors.blueBackground,
            borderRadius: BorderRadius.circular(20),
            boxShadow: [AppTheme.cardShadow],
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Expanded(child: _buildStatItem("Уровень", user.level.toString())),
              Expanded(
                child: GestureDetector(
                  onTap: () {
                    showDialog(
                      context: context,
                      builder: (context) => const AvatarSelectionDialog(),
                    );
                  },
                  child: CircleAvatar(
                    radius: 60,
                    backgroundColor: AppColors.white,
                    child: SecureNetworkImage(
                      imageUrl: userAvatar.url,
                      fit: BoxFit.cover,
                    ),
                  ),
                ),
              ),
              Expanded(child: _buildStatItem("Очки", '${user.currentXp} XP')),
            ],
          ),
        );
      },
    );
  }

  Widget _buildStatItem(String label, String value) {
    return Column(
      children: [
        Text(label, style: AppTextStyles.bodyLarge.copyWith(fontSize: 22)),
        Text(value, style: AppTextStyles.headlineLarge.copyWith(fontSize: 22)),
      ],
    );
  }
}
