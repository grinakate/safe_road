import 'package:flutter/material.dart';

import '../models/topic.dart';
import '../models/topic_status.dart';
import '../theme.dart';

class TopicItem extends StatelessWidget {
  final Topic topic;
  final int sectionId;
  final int index;
  final bool rightDirection;

  static final alignmentsToRight = [
    Alignment(-0.5, 0.0),
    Alignment.center,
    Alignment(0.5, 0.0),
  ];

  static final alignmentsToLeft = [Alignment.centerLeft, Alignment.centerRight];

  const TopicItem({
    super.key,
    required this.topic,
    required this.sectionId,
    required this.index,
    required this.rightDirection,
  });

  @override
  Widget build(BuildContext context) {
    // Смещение: центр/право/центр/лево по индексу
    final align = rightDirection
        ? alignmentsToRight[index]
        : alignmentsToLeft[index];
    final status = topic.status;
    final color = status == TopicStatus.LOCKED
        ? AppColors.brownBorder
        : AppColors.primaryGreen;
    final icon = status == TopicStatus.LOCKED
        ? Icons.lock
        : (status == TopicStatus.COMPLETED ? Icons.check : null);
    final iconColor = icon == Icons.lock ? AppColors.darkBrownText : AppColors.white;

    return Align(
      alignment: align,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 18, horizontal: 24),
        child: Column(
          children: [
            GestureDetector(
              onTap: status == TopicStatus.LOCKED
                  ? null
                  : () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Открываем: ${topic.title}')),
                      );
                    },
              child: Stack(
                alignment: Alignment.center, // Центрируем элементы по умолчанию
                children: [
                  // Сам круглый Container
                  Container(
                    width: 72,
                    height: 72,
                    decoration: BoxDecoration(
                      color: color,
                      shape: BoxShape.circle,
                      boxShadow: [
                        BoxShadow(
                          color: icon == Icons.lock
                              ? AppColors.darkBrownText.withAlpha(64)
                              : AppColors.white.withAlpha(128),
                          blurRadius: 9,
                          offset: const Offset(0, 0),
                        ),
                      ],
                    ),
                  ),
                  // Иконка, позиционированная в верхнем правом углу
                  icon == Icons.lock
                      ? Positioned(
                          top: 0, // Сдвигаем иконку на 0px сверху
                          right: 0, // Сдвигаем иконку на 0px справа
                          child: Icon(icon, color: iconColor, size: 32),
                        )
                      : Icon(icon, color: iconColor, size: 32),
                  Text(
                    topic.orderIndex.toString(),
                    textAlign: TextAlign.center,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: AppTheme.topicNumber,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 0),
            SizedBox(
              width: 120,
              child: Text(
                topic.title,
                textAlign: TextAlign.center,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: AppTheme.topicName,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
