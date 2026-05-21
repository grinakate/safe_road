import 'package:flutter/material.dart';

import '../theme.dart';

class RoadPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = AppColors.brownBorder
      ..style = PaintingStyle.stroke
      ..strokeWidth = 40
      ..strokeCap = StrokeCap.round
      ..strokeJoin = StrokeJoin.round;

    final inner = Paint()
      ..color = AppColors.white
      ..style = PaintingStyle.stroke
      ..strokeWidth = 3
      ..strokeCap = StrokeCap.round;

    final path = Path();
    double cx = size.width * 0.5;
    double cy = 20;
    path.moveTo(cx, cy);

    final seg = 160.0;
    final bend = size.width * 0.28;
    int count = (size.height / seg).ceil();

    for (int i = 0; i < count; i++) {
      final endY = ((i + 1) * seg).clamp(0.0, size.height);
      final bool toRight = i % 2 == 0;
      final tx = toRight ? cx + bend : cx - bend;
      final c1x = cx + (toRight ? bend * 0.7 : -bend * 0.7);
      final c1y = cy + seg * 0.35;
      final c2x = tx + (toRight ? -bend * 0.4 : bend * 0.4);
      final c2y = cy + seg * 0.75;

      path.cubicTo(c1x, c1y, c2x, c2y, tx, endY);
      cx = tx;
      cy = endY;
      if (cy >= size.height) break;
    }

    canvas.drawPath(path, paint);
    canvas.drawPath(path, inner);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
