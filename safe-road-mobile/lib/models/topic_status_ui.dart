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
  // New properties
  final bool showShadow;
  final Color borderColor;
  final double borderWidth;
  final TextStyle? textStyle;

  TopicStatusUiConfig({
    required this.circleColor,
    required this.icon,
    required this.iconColor,
    required this.shadowColor,
    required this.isTapEnabled,
    required this.showOrderIndex,
    required this.placeIconTopRight,
    this.showShadow = true,
    this.borderColor = Colors.transparent,
    this.borderWidth = 0.0,
    this.textStyle,
  });
}

TopicStatusUiConfig resolveTopicStatusUi(TopicStatus status) {
  switch (status) {
    case TopicStatus.LOCKED:
      return TopicStatusUiConfig(
        circleColor: AppColors.greyBorder,
        icon: Icons.lock,
        iconColor: AppColors.darkBrownText,
        shadowColor: AppColors.darkBrownText,
        isTapEnabled: false,
        showOrderIndex: true,
        placeIconTopRight: true,
        showShadow: false,
        borderColor: AppColors.greyIcon,
        borderWidth: 1,
        textStyle: AppTextStyles.topicNumber,
      );
    case TopicStatus.UNLOCKED:
      return TopicStatusUiConfig(
        circleColor: AppColors.primaryGreen,
        icon: null,
        iconColor: AppColors.white,
        shadowColor: AppColors.green,
        isTapEnabled: true,
        showOrderIndex: true,
        placeIconTopRight: false,
        showShadow: true,
        borderColor: AppColors.darkGreen,
        borderWidth: 1.5,
        textStyle: AppTextStyles.topicNumber,
      );
    case TopicStatus.COMPLETED:
      return TopicStatusUiConfig(
        circleColor: AppColors.primaryGreen,
        icon: Icons.check,
        iconColor: AppColors.white,
        shadowColor: AppColors.white,
        isTapEnabled: true,
        showOrderIndex: false,
        placeIconTopRight: false,
        showShadow: false,
        borderColor: AppColors.darkGreen,
        borderWidth: 0.5,
        textStyle: AppTextStyles.topicNumber,
      );
  }
}


