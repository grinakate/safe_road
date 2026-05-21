import 'package:flutter/material.dart';

import 'topic_status.dart';
import '../theme.dart';

class TopicStatusUiConfig {
  final Color circleColor;
  final IconData? icon;
  final Color iconColor;
  final Color shadowColor;
  final bool isTapEnabled;
  final bool showOrderIndex;
  final bool placeIconTopRight;

  const TopicStatusUiConfig({
    required this.circleColor,
    required this.icon,
    required this.iconColor,
    required this.shadowColor,
    required this.isTapEnabled,
    required this.showOrderIndex,
    required this.placeIconTopRight,
  });
}

TopicStatusUiConfig resolveTopicStatusUi(TopicStatus status) {
  switch (status) {
    case TopicStatus.LOCKED:
      return const TopicStatusUiConfig(
        circleColor: AppColors.greyBorder,
        icon: Icons.lock,
        iconColor: AppColors.darkBrownText,
        shadowColor: AppColors.darkBrownText,
        isTapEnabled: false,
        showOrderIndex: true,
        placeIconTopRight: true,
      );
    case TopicStatus.UNLOCKED:
      return const TopicStatusUiConfig(
        circleColor: AppColors.primaryGreen,
        icon: null,
        iconColor: AppColors.white,
        shadowColor: AppColors.white,
        isTapEnabled: true,
        showOrderIndex: true,
        placeIconTopRight: false,
      );
    case TopicStatus.COMPLETED:
      return const TopicStatusUiConfig(
        circleColor: AppColors.primaryGreen,
        icon: Icons.check,
        iconColor: AppColors.white,
        shadowColor: AppColors.white,
        isTapEnabled: true,
        showOrderIndex: false,
        placeIconTopRight: false,
      );
  }
}


