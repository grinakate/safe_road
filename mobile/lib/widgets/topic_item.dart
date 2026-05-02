import 'package:flutter/material.dart';
import '../models/topic.dart';
import '../models/topic_status.dart';

class TopicItem extends StatelessWidget {
  final Topic topic;

  const TopicItem({super.key, required this.topic});

  @override
  Widget build(BuildContext context) {
    // Смещение: центр/право/центр/лево по индексу — реализуем через orderIndex (mod 4)
    final idx = topic.orderIndex % 4;
    final alignments = [
      Alignment.center,
      Alignment.centerRight,
      Alignment.center,
      Alignment.centerLeft,
    ];
    final align = alignments[idx];

    final status = topic.status;
    final color = status == TopicStatus.COMPLETED
        ? Colors.orange
        : status == TopicStatus.UNLOCKED
        ? Colors.green
        : Colors.grey[300];
    final icon = status == TopicStatus.LOCKED
        ? Icons.lock
        : (status == TopicStatus.COMPLETED ? Icons.check : Icons.play_arrow);

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
                      // TODO: навигация в тему
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text('Открываем: ${topic.name}')),
                      );
                    },
              child: Container(
                width: 72,
                height: 72,
                decoration: BoxDecoration(
                  color: color,
                  shape: BoxShape.circle,
                  border: Border.all(color: Colors.white, width: 4),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.25),
                      blurRadius: 8,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: Icon(icon, color: Colors.white, size: 32),
              ),
            ),
            const SizedBox(height: 8),
            SizedBox(
              width: 120,
              child: Text(
                topic.name,
                textAlign: TextAlign.center,
                maxLines: 2,
                overflow: TextOverflow.ellipsis,
                style: const TextStyle(fontSize: 13),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
