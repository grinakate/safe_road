import 'package:flutter/material.dart';

class AppTheme {
  static const Color primaryColor = Colors.green;
  static const Color secondaryColor = Colors.brown;
  static const Color borderColor = Colors.brown;

  static const TextStyle headerTextStyle = TextStyle(
    fontSize: 32,
    fontWeight: FontWeight.bold,
    color: borderColor,
  );

  static const TextStyle subHeaderTextStyle = TextStyle(
    fontSize: 16,
    color: secondaryColor,
  );

  // Утилитарный общий стиль. Раньше здесь указывался Nunito напрямую,
  // теперь шрифт задаётся глобально через ThemeData.fontFamily.
  // Используйте AppTheme.nunitoTextStyle.copyWith(...) чтобы задать размер, цвет и прочие свойства.
  static const TextStyle nunitoTextStyle = TextStyle();

  // Общие повторяющиеся стили
  static const TextStyle bodySmall = TextStyle(
    fontSize: 11,
  );

  static const TextStyle body14 = TextStyle(
    fontSize: 14,
    color: borderColor,
  );

  static const TextStyle sectionHeaderText = TextStyle(
    color: Colors.white,
    fontSize: 18,
    fontWeight: FontWeight.w600,
  );

  static const TextStyle topicNumber = TextStyle(
    color: Colors.white,
    fontSize: 24,
  );

  static const TextStyle topicName = TextStyle(
    color: borderColor,
    fontSize: 14,
  );

  static const TextStyle appBarTitle = TextStyle(
    color: Colors.brown,
    fontSize: 22,
    fontWeight: FontWeight.bold,
  );

  static const TextStyle achievementTitle = TextStyle(
    fontSize: 22,
    fontWeight: FontWeight.bold,
    color: Colors.black87,
  );

  static const TextStyle achievementXp = TextStyle(
    fontSize: 18,
    fontWeight: FontWeight.w600,
    color: Colors.amber,
  );

  static const TextStyle achievementDesc = TextStyle(
    fontSize: 16,
    color: Colors.grey,
  );

  static const TextStyle buttonWhiteBold = TextStyle(
    fontSize: 16,
    color: Colors.white,
    fontWeight: FontWeight.bold,
  );

  static const InputDecoration inputDecoration = InputDecoration(
    filled: true,
    fillColor: Colors.white,
    labelStyle: TextStyle(color: secondaryColor),
    hintStyle: TextStyle(color: secondaryColor),
    border: OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12)),
    ),
    enabledBorder: OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12)),
      borderSide: BorderSide(color: secondaryColor, width: 1),
    ),
    focusedBorder: OutlineInputBorder(
      borderRadius: BorderRadius.all(Radius.circular(12)),
      borderSide: BorderSide(color: borderColor, width: 2),
    ),
  );

  static ThemeData get themeData => ThemeData(
        useMaterial3: true,
        fontFamily: 'Nunito',
        colorSchemeSeed: primaryColor,
        textTheme: const TextTheme(
          headlineLarge: headerTextStyle,
          titleMedium: subHeaderTextStyle,
        ),
        inputDecorationTheme: InputDecorationTheme(
          filled: true,
          fillColor: Colors.white,
          labelStyle: TextStyle(color: secondaryColor),
          hintStyle: TextStyle(color: secondaryColor),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.all(Radius.circular(12)),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.all(Radius.circular(12)),
            borderSide: BorderSide(color: secondaryColor, width: 1),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.all(Radius.circular(12)),
            borderSide: BorderSide(color: borderColor, width: 2),
          ),
        ),
      );

  static InputDecorationThemeData get inputDecorationTheme => themeData.inputDecorationTheme!;
}




