import 'package:flutter/material.dart';

import '../ui/theme/app_theme.dart';

/// Показывает системный выбор даты с единым стилем приложения
Future<DateTime?> showAppDatePicker({
  required BuildContext context,
  required DateTime initialDate,
  required DateTime firstDate,
  required DateTime lastDate,
}) {
  return showDatePicker(
    context: context,
    initialDate: initialDate,
    firstDate: firstDate,
    lastDate: lastDate,
    locale: const Locale('ru', 'RU'),
    builder: (BuildContext context, Widget? child) {
      final currentTheme = Theme.of(context);

      return Theme(
        data: currentTheme.copyWith(
          datePickerTheme: DatePickerThemeData(
            backgroundColor: AppColors.white,
            headerBackgroundColor: AppColors.primaryGreen,
            headerForegroundColor: AppColors.white,
            dayForegroundColor: WidgetStateProperty.resolveWith((states) {
              if (states.contains(WidgetState.selected)) {
                return AppColors.white; // Цвет выбранного дня
              }
              return AppColors.darkBrownText; // Цвет обычных дней
            }),
            todayForegroundColor: WidgetStateProperty.all(
              AppColors.primaryGreen,
            ),
            dayOverlayColor: WidgetStateProperty.all(
              AppColors.primaryGreen.withValues(alpha: 0.1),
            ),
          ),
          // Темы для кнопок "ОК" и "Отмена"
          textButtonTheme: TextButtonThemeData(
            style: TextButton.styleFrom(
              foregroundColor: AppColors.darkBrownText, // Цвет кнопок действий
            ),
          ),
          dialogTheme: const DialogThemeData(backgroundColor: AppColors.white),
        ),
        child: child ?? const SizedBox.shrink(),
      );
    },
  );
}
