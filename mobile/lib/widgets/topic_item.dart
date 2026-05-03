import 'package:flutter/material.dart';
import 'package:safe_road/core/constants.dart';

import '../models/topic.dart';
import '../models/topic_status.dart';

class TopicItem extends StatelessWidget {
  final Topic topic;

  const TopicItem({super.key, required this.topic});

  @override
  Widget build(BuildContext context) {
    // Смещение: центр/право/центр/лево по индексу — реализуем через orderIndex (mod 4)
    final idx = topic.orderIndex % 5;
    final alignments = [
      Alignment(-0.5, 0.0),
      Alignment.centerLeft,
      Alignment.center,
      Alignment.centerRight,
      Alignment(0.5, 0.0),
    ];
    final align = alignments[idx];

    final status = topic.status;
    final color = status == TopicStatus.LOCKED
        ? Colors.grey.shade400
        : AppConstants.greenTestColor;
    final icon = status == TopicStatus.LOCKED
        ? Icons.lock
        : (status == TopicStatus.COMPLETED ? Icons.check : null);
    final iconColor = icon == Icons.lock ? Colors.brown : Colors.white;

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
                        SnackBar(content: Text('Открываем: ${topic.name}')),
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
                              ? Colors.black.withOpacity(0.25)
                              : Colors.white.withOpacity(0.5),
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
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 24,
                      fontFamily: 'Nunito',
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 0),
            SizedBox(
              width: 120,
              child: Text(
                topic.name,
                textAlign: TextAlign.center,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(
                  color: AppConstants.borderColor,
                  fontSize: 14,
                  fontFamily: 'Nunito',
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
