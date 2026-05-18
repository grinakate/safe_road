import 'package:flutter/material.dart';

import 'app_colors.dart';
import 'app_text_styles.dart';

class AppTheme {
  static const InputDecoration _sharedInputDecoration = InputDecoration(
    filled: true,
    fillColor: AppColors.white,
    contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 14),
    labelStyle: AppTextStyles.inputLabel,
    hintStyle: AppTextStyles.inputHint,
    border: OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12.0)),
      borderSide: BorderSide(color: AppColors.greyBorder),
    ),
    enabledBorder: OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12.0)),
      borderSide: BorderSide(color: AppColors.greyBorder, width: 1),
    ),
    focusedBorder: OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12.0)),
      borderSide: BorderSide(color: AppColors.primaryGreen, width: 2),
    ),
  );

  static const InputDecoration inputDecoration = _sharedInputDecoration;

  static ThemeData get lightTheme {
    final colorScheme = ColorScheme.fromSeed(
      seedColor: AppColors.primaryGreen,
      brightness: Brightness.light,
    ).copyWith(
      primary: AppColors.primaryGreen,
      secondary: AppColors.orangeCatAccent,
      error: AppColors.errorRed,
      surface: AppColors.white,
    );

    return ThemeData(
      useMaterial3: true,
      colorScheme: colorScheme,
      scaffoldBackgroundColor: AppColors.lightBlueBackground,
      fontFamily: 'Montserrat',
      fontFamilyFallback: const ['Roboto'],
      textTheme: const TextTheme(
        headlineLarge: AppTextStyles.headlineLarge,
        titleLarge: AppTextStyles.titleLarge,
        titleMedium: AppTextStyles.appBarTitle,
        bodyLarge: AppTextStyles.bodyLarge,
        bodyMedium: AppTextStyles.bodyMedium,
        labelLarge: AppTextStyles.buttonText,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primaryGreen,
          foregroundColor: AppColors.white,
          textStyle: AppTextStyles.buttonText,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12.0),
          ),
          minimumSize: const Size.fromHeight(50),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
        ),
      ),
      inputDecorationTheme: const InputDecorationTheme(
        filled: true,
        fillColor: AppColors.white,
        contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        labelStyle: AppTextStyles.inputLabel,
        hintStyle: AppTextStyles.inputHint,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.all(Radius.circular(12.0)),
          borderSide: BorderSide(color: AppColors.greyBorder),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.all(Radius.circular(12.0)),
          borderSide: BorderSide(color: AppColors.greyBorder, width: 1),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.all(Radius.circular(12.0)),
          borderSide: BorderSide(color: AppColors.primaryGreen, width: 2),
        ),
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.lightBlueBackground,
        foregroundColor: AppColors.darkBrownText,
        elevation: 0,
        centerTitle: true,
        titleTextStyle: AppTextStyles.appBarTitle,
        iconTheme: IconThemeData(color: AppColors.darkBrownText),
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: AppColors.white,
        selectedItemColor: AppColors.primaryGreen,
        unselectedItemColor: AppColors.greyIcon,
        selectedLabelStyle: AppTextStyles.bottomNavSelected,
        unselectedLabelStyle: AppTextStyles.bottomNavUnselected,
        elevation: 8,
      ),
      cardTheme: const CardThemeData(
        color: AppColors.white,
        elevation: 2,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.all(Radius.circular(12.0)),
        ),
        margin: EdgeInsets.all(12),
      ),
    );
  }

  // Backward-compatible shortcuts for existing code.
  static const TextStyle headerTextStyle = AppTextStyles.headlineLarge;
  static const TextStyle subHeaderTextStyle = AppTextStyles.bodyLarge;
  static const TextStyle appBarTitle = AppTextStyles.appBarTitle;
  static const TextStyle buttonWhiteBold = AppTextStyles.buttonText;
  static const TextStyle bodySmall = AppTextStyles.bodySmall;
  static const TextStyle body14 = AppTextStyles.body14;
  static const TextStyle sectionHeaderText = AppTextStyles.sectionHeaderText;
  static const TextStyle topicNumber = AppTextStyles.topicNumber;
  static const TextStyle topicName = AppTextStyles.topicName;
  static const TextStyle achievementTitle = AppTextStyles.achievementTitle;
  static const TextStyle achievementXp = AppTextStyles.achievementXp;
  static const TextStyle achievementDesc = AppTextStyles.achievementDesc;
}



