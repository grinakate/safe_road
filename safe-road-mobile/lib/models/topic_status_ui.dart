import 'package:flutter/material.dart';

import 'topic_status.dart';
import '../theme.dart';

class TopicStatusUiConfig {
  final Color circleColor;
  final Color shadowColor;
  final bool isTapEnabled;
  final bool showOrderIndex;
  // Shadow and border properties
  final bool showShadow;
  final Color borderColor;
  final double borderWidth;
  // Text style for order index
  final TextStyle? textStyle;
  // Icon in center (e.g., check for completed, cup for final test)
  final IconData? centerIcon;
  final Color? centerIconColor;
  // Badge icon (e.g., lock in top-right for locked)
  final IconData? badgeIcon;
  final Color? badgeIconColor;

  TopicStatusUiConfig({
    required this.circleColor,
    required this.shadowColor,
    required this.isTapEnabled,
    required this.showOrderIndex,
    this.showShadow = true,
    this.borderColor = Colors.transparent,
    this.borderWidth = 0.0,
    this.textStyle,
    this.centerIcon,
    this.centerIconColor,
    this.badgeIcon,
    this.badgeIconColor,
  });
}

TopicStatusUiConfig resolveTopicStatusUi(
  TopicStatus status, {
  bool isFinalTest = false,
  bool isFinalTestAvailable = true,
}) {
  // Отдельный конфиг для финального теста
  if (isFinalTest) {
    if (isFinalTestAvailable) {
      return TopicStatusUiConfig(
        circleColor: AppColors.primaryGreen,
        shadowColor: AppColors.white,
        isTapEnabled: true,
        showOrderIndex: false,
        showShadow: true,
        borderColor: Colors.transparent,
        borderWidth: 0.0,
        textStyle: null,
        centerIcon: Icons.emoji_events, // кубок в центре
        centerIconColor: AppColors.white,
        badgeIcon: null,
        badgeIconColor: null,
      );
    }

    return TopicStatusUiConfig(
      circleColor: AppColors.greyBorder,
      shadowColor: AppColors.darkBrownText,
      isTapEnabled: false,
      showOrderIndex: false,
      showShadow: false,
      borderColor: AppColors.greyIcon,
      borderWidth: 1,
      textStyle: null,
      centerIcon: Icons.emoji_events, // кубок в центре
      centerIconColor: AppColors.white,
      badgeIcon: Icons.lock,
      badgeIconColor: AppColors.darkBrownText,
    );
  }

  switch (status) {
    case TopicStatus.LOCKED:
      return TopicStatusUiConfig(
        circleColor: AppColors.greyBorder,
        shadowColor: AppColors.darkBrownText,
        isTapEnabled: false,
        showOrderIndex: true,
        showShadow: false,
        borderColor: AppColors.greyIcon,
        borderWidth: 1,
        textStyle: AppTextStyles.topicNumber,
        centerIcon: null,
        centerIconColor: null,
        badgeIcon: Icons.lock, // замок в правом верхнем углу
        badgeIconColor: AppColors.darkBrownText,
      );
    case TopicStatus.UNLOCKED:
      return TopicStatusUiConfig(
        circleColor: AppColors.primaryGreen,
        shadowColor: AppColors.green,
        isTapEnabled: true,
        showOrderIndex: true,
        showShadow: true,
        borderColor: AppColors.darkGreen,
        borderWidth: 1.5,
        textStyle: AppTextStyles.topicNumber,
        centerIcon: null,
        centerIconColor: null,
        badgeIcon: null,
        badgeIconColor: null,
      );
    case TopicStatus.COMPLETED:
      return TopicStatusUiConfig(
        circleColor: AppColors.primaryGreen,
        shadowColor: AppColors.white,
        isTapEnabled: true,
        showOrderIndex: false,
        showShadow: false,
        borderColor: AppColors.darkGreen,
        borderWidth: 0.5,
        textStyle: AppTextStyles.topicNumber,
        centerIcon: Icons.check, // галочка в центре
        centerIconColor: AppColors.white,
        badgeIcon: null,
        badgeIconColor: null,
      );
  }
}


