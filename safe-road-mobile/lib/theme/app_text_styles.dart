import 'package:flutter/material.dart';

import 'app_colors.dart';

class AppTextStyles {
  static const String _fontFamily = 'Nunito';
  static const List<String> _fontFallback = ['Roboto'];

  static const TextStyle headlineLarge = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 28,
    fontWeight: FontWeight.w800,
    color: AppColors.darkBrownText,
  );

  static const TextStyle titleLarge = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 22,
    fontWeight: FontWeight.w600,
    color: AppColors.darkBrownText,
  );

  static const TextStyle appBarTitle = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 20,
    fontWeight: FontWeight.w500,
    color: AppColors.darkBrownText,
  );

  static const TextStyle bodyLarge = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 16,
    fontWeight: FontWeight.normal,
    color: AppColors.darkBrownText,
  );

  static const TextStyle bodyMedium = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 14,
    fontWeight: FontWeight.normal,
    color: AppColors.darkBrownText,
  );

  static const TextStyle buttonText = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 18,
    fontWeight: FontWeight.w800,
    color: AppColors.white,
  );

  static const TextStyle inputLabel = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 16,
    fontWeight: FontWeight.normal,
    color: AppColors.brownText,
  );

  static const TextStyle inputHint = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 16,
    fontWeight: FontWeight.normal,
    color: AppColors.brownText,
  );

  static const TextStyle bottomNavSelected = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 12,
    fontWeight: FontWeight.bold,
    color: AppColors.primaryGreen,
  );

  static const TextStyle bottomNavUnselected = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 12,
    fontWeight: FontWeight.normal,
    color: AppColors.darkBrownIcon,
  );

  static const TextStyle errorText = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 14,
    fontWeight: FontWeight.w500,
    color: AppColors.errorRed,
  );

  static const TextStyle sectionHeaderText = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 18,
    fontWeight: FontWeight.w600,
    color: AppColors.darkBrownText,
  );

  static const TextStyle topicNumber = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 24,
    fontWeight: FontWeight.normal,
    color: AppColors.white,
  );

  static const TextStyle topicName = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 14,
    fontWeight: FontWeight.normal,
    color: AppColors.darkBrownText,
  );

  static const TextStyle achievementTitle = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 22,
    fontWeight: FontWeight.bold,
    color: AppColors.darkBrownText,
  );

  static const TextStyle achievementXp = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 18,
    fontWeight: FontWeight.w600,
    color: AppColors.orangeCatAccent,
  );

  static const TextStyle achievementDesc = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 16,
    fontWeight: FontWeight.normal,
    color: AppColors.brownText,
  );

  static const TextStyle bodySmall = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 11,
    fontWeight: FontWeight.normal,
    color: AppColors.brownText,
  );

  static const TextStyle body14 = TextStyle(
    fontFamily: _fontFamily,
    fontFamilyFallback: _fontFallback,
    fontSize: 14,
    fontWeight: FontWeight.normal,
    color: AppColors.darkBrownText,
  );
}


