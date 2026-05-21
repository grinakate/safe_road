import 'package:flutter/material.dart';

import '../theme.dart';

/// Показывает системный выбор даты с единым приложенным стилем:
/// - русская локаль
/// - белый фон диалога
/// - тёмно-коричневый цвет текста и кнопок (AppColors.darkBrownText)
/// Возвращает выбранную дату или null.
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
      return Theme(
        data: Theme.of(context).copyWith(
          // Dialog background (deprecated property but safer across SDK versions)
          colorScheme: Theme.of(context).colorScheme.copyWith(
            primary: AppColors.primaryGreen,
            onSurface: AppColors.darkBrownText,
          ),
          // Buttons color
          textButtonTheme: TextButtonThemeData(
            style: TextButton.styleFrom(
              foregroundColor: AppColors.darkBrownText,
            ),
          ),
          // DatePicker theme for Material 3
          datePickerTheme: DatePickerThemeData(
            backgroundColor: AppColors.white,
          ),
          dialogTheme: DialogThemeData(backgroundColor: AppColors.white),
        ),
        child: child ?? const SizedBox.shrink(),
      );
    },
  );
}
