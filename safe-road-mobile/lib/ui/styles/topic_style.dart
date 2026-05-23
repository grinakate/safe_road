import 'package:flutter/material.dart';

import '../../data/models/learning/topic_status.dart';
import '../theme/app_theme.dart';

class TopicStyle {
  final Color circleColor;
  final Color shadowColor;
  final bool isTapEnabled;
  final bool showOrderIndex;
  final bool showShadow;
  final Color borderColor;
  final double borderWidth;
  final IconData? centerIcon;
  final IconData? badgeIcon;

  const TopicStyle({
    required this.circleColor,
    required this.shadowColor,
    required this.isTapEnabled,
    required this.showOrderIndex,
    this.showShadow = true,
    this.borderColor = Colors.transparent,
    this.borderWidth = 0.0,
    this.centerIcon,
    this.badgeIcon,
  });

  factory TopicStyle.locked() => const TopicStyle(
    circleColor: AppColors.greyBorder,
    shadowColor: AppColors.darkBrownText,
    isTapEnabled: false,
    showOrderIndex: true,
    showShadow: false,
    borderColor: AppColors.greyIcon,
    borderWidth: 1,
    badgeIcon: Icons.lock,
  );

  factory TopicStyle.unlocked() => const TopicStyle(
    circleColor: AppColors.primaryGreen,
    shadowColor: AppColors.green,
    isTapEnabled: true,
    showOrderIndex: true,
    showShadow: true,
    borderColor: AppColors.darkGreen,
    borderWidth: 1.5,
  );

  factory TopicStyle.completed() => const TopicStyle(
    circleColor: AppColors.primaryGreen,
    shadowColor: AppColors.white,
    isTapEnabled: true,
    showOrderIndex: false,
    showShadow: false,
    borderColor: AppColors.darkGreen,
    borderWidth: 0.5,
    centerIcon: Icons.check,
  );

  static TopicStyle resolve({
    required TopicStatus status,
    bool isFinalTest = false,
    bool isFinalTestAvailable = true,
  }) {
    if (isFinalTest) {
      return isFinalTestAvailable
          ? const TopicStyle(
              circleColor: AppColors.primaryGreen,
              shadowColor: AppColors.white,
              isTapEnabled: true,
              showOrderIndex: false,
              showShadow: true,
              centerIcon: Icons.emoji_events,
            )
          : TopicStyle.locked();
    }

    return switch (status) {
      TopicStatus.LOCKED => TopicStyle.locked(),
      TopicStatus.UNLOCKED => TopicStyle.unlocked(),
      TopicStatus.COMPLETED => TopicStyle.completed(),
    };
  }
}
