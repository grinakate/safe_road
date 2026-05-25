import 'package:flutter/material.dart';

import '../../data/models/learning/topic_status.dart';
import '../theme/app_theme.dart';

class TopicStyle {
  final Color circleColor;
  final bool isTapEnabled;
  final bool showOrderIndex;
  final bool showShadow;
  final Color shadowColor;
  final double borderWidth;
  final Color borderColor;
  final Widget? content;

  static final Widget lockIcon = Positioned(
    top: 0,
    right: 0,
    child: Icon(Icons.lock, color: AppColors.darkBrownIcon, size: 24),
  );

  const TopicStyle({
    required this.circleColor,
    required this.isTapEnabled,
    required this.showOrderIndex,
    required this.shadowColor,
    this.showShadow = true,
    this.borderWidth = 0.0,
    this.borderColor = Colors.transparent,
    this.content,
  });

  factory TopicStyle.locked() => TopicStyle(
    circleColor: AppColors.greyBorder,
    isTapEnabled: false,
    showOrderIndex: true,
    showShadow: false,
    shadowColor: AppColors.darkBrownText,
    borderColor: AppColors.greyIcon,
    borderWidth: 1,
    content: lockIcon,
  );

  factory TopicStyle.unlocked() => const TopicStyle(
    circleColor: AppColors.primaryGreen,
    isTapEnabled: true,
    showOrderIndex: true,
    showShadow: true,
    shadowColor: AppColors.green,
    borderColor: AppColors.darkGreen,
    borderWidth: 1.5,
  );

  factory TopicStyle.completed() => const TopicStyle(
    circleColor: AppColors.primaryGreen,
    isTapEnabled: true,
    shadowColor: AppColors.white,
    showOrderIndex: false,
    showShadow: false,
    borderColor: AppColors.darkGreen,
    borderWidth: 0.5,
    content: Icon(Icons.check, color: AppColors.white, size: 28),
  );

  factory TopicStyle.finalUnlockTest() => const TopicStyle(
    circleColor: AppColors.primaryGreen,
    isTapEnabled: true,
    shadowColor: AppColors.green,
    showOrderIndex: false,
    showShadow: true,
    borderColor: AppColors.darkGreen,
    borderWidth: 1.5,
    content: Icon(Icons.emoji_events, color: AppColors.white, size: 28),
  );

  factory TopicStyle.finalLockTest() => TopicStyle(
    circleColor: AppColors.greyBorder,
    isTapEnabled: false,
    shadowColor: AppColors.green,
    showOrderIndex: false,
    showShadow: false,
    borderColor: AppColors.greyIcon,
    borderWidth: 0.5,
    content: SizedBox.expand(
      child: Stack(
        alignment: Alignment.center,
        children: [
          const Icon(Icons.emoji_events, color: AppColors.white, size: 28),
          lockIcon
        ],
      ),
    ),
  );

  static TopicStyle resolve({
    required TopicStatus status,
    bool isFinalTest = false,
    bool isFinalTestAvailable = true,
  }) {
    if (isFinalTest) {
      return isFinalTestAvailable
          ? TopicStyle.finalUnlockTest()
          : TopicStyle.finalLockTest();
    }

    return switch (status) {
      TopicStatus.LOCKED => TopicStyle.locked(),
      TopicStatus.UNLOCKED => TopicStyle.unlocked(),
      TopicStatus.COMPLETED => TopicStyle.completed(),
    };
  }
}
