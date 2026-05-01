import 'package:flutter/material.dart';

class RoadPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    // Стиль для основной дороги
    final paint = Paint()
      ..color =
          Colors.grey[400]! // Цвет дороги
      ..style = PaintingStyle.stroke
      ..strokeWidth =
          40.0 // Толщина дороги
      ..strokeCap = StrokeCap
          .round // Закругленные концы линий
      ..strokeJoin = StrokeJoin.round; // Плавные соединения сегментов

    // Стиль для пунктирной линии
    final dashPaint = Paint()
      ..color = Colors
          .white // Цвет пунктира
      ..style = PaintingStyle.stroke
      ..strokeWidth =
          2.0 // Толщина пунктира
      ..strokeCap = StrokeCap.round;

    final path = Path();

    // Переменные для отслеживания текущей точки пути
    double currentPathX = size.width * 0.5; // Начинаем от центра по X
    double currentPathY = 0; // Начинаем от самого верха

    path.moveTo(
      currentPathX,
      currentPathY,
    ); // Устанавливаем начальную точку пути

    // Параметры для формирования изгибов дороги
    double segmentHeight = 150.0; // Высота одного "изгиба" дороги в пикселях
    double horizontalBend =
        size.width *
        0.3; // Насколько сильно дорога будет отклоняться вбок (0.3 от ширины экрана)

    // Сколько сегментов (изгибов) поместится на заданной высоте холста
    int numSegments = (size.height / segmentHeight).ceil();

    for (int i = 0; i < numSegments; i++) {
      double endY =
          (i + 1) *
          segmentHeight; // Конечная Y-координата для текущего сегмента
      if (endY > size.height) {
        endY = size.height; // Не выходим за пределы холста
      }

      double targetX; // X-координата конечной точки сегмента
      double control1X,
          control1Y,
          control2X,
          control2Y; // Контрольные точки для Cubic Bezier

      if (i % 2 == 0) {
        // Сегмент изгибается вправо
        targetX = size.width * 0.5 + horizontalBend;

        // Первая контрольная точка: тянем вправо и чуть вниз от текущей
        control1X = currentPathX + horizontalBend * 0.8;
        control1Y = currentPathY + segmentHeight * 0.3;

        // Вторая контрольная точка: тянем к конечной точке, но чуть правее центра Y-сегмента
        control2X = targetX - horizontalBend * 0.4;
        control2Y = currentPathY + segmentHeight * 0.7;
      } else {
        // Сегмент изгибается влево
        targetX = size.width * 0.5 - horizontalBend;

        // Первая контрольная точка: тянем влево и чуть вниз от текущей
        control1X = currentPathX - horizontalBend * 0.8;
        control1Y = currentPathY + segmentHeight * 0.3;

        // Вторая контрольная точка: тянем к конечной точке, но чуть левее центра Y-сегмента
        control2X = targetX + horizontalBend * 0.4;
        control2Y = currentPathY + segmentHeight * 0.7;
      }

      // Ограничиваем X-координаты, чтобы они не выходили за пределы экрана
      control1X = control1X.clamp(0.0, size.width);
      control2X = control2X.clamp(0.0, size.width);
      targetX = targetX.clamp(0.0, size.width);

      // Добавляем сегмент Cubic Bezier кривой к пути
      path.cubicTo(control1X, control1Y, control2X, control2Y, targetX, endY);

      // Обновляем текущую позицию для следующего сегмента
      currentPathX = targetX;
      currentPathY = endY;

      if (currentPathY >= size.height)
        break; // Останавливаем, если достигли конца холста
    }

    // Если последняя точка пути не находится точно по центру и не достигла конца холста,
    // добавляем прямую линию до центра нижней части холста для завершения.
    if (currentPathX != size.width * 0.5 && currentPathY < size.height) {
      path.lineTo(size.width * 0.5, size.height);
    }

    canvas.drawPath(path, paint); // Рисуем основную дорогу
    canvas.drawPath(path, dashPaint); // Рисуем пунктирную линию
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false; // false, так как дорога статична
}
