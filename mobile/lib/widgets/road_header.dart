import 'package:flutter/material.dart';

class RoadHeader extends StatelessWidget {
  const RoadHeader({super.key});

  @override
  Widget build(BuildContext context) {
    // В реале значения берутся из профиля / API
    final progress = 0.25; // пример

    return Container(
      color: Colors.white,
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
      child: Column(
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 22,
                backgroundColor: Colors.orange[100],
                child: const Icon(Icons.pets, color: Colors.orange),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      '150 очков до следующего уровня',
                      style: TextStyle(fontSize: 12),
                    ),
                    const SizedBox(height: 6),
                    ClipRRect(
                      borderRadius: BorderRadius.circular(6),
                      child: LinearProgressIndicator(
                        value: progress,
                        minHeight: 10,
                        color: Colors.green,
                        backgroundColor: Colors.grey[200],
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: const [
              Text(
                'Уровень 1: 0 очков',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              Text('0/71 Уроков пройдено'),
            ],
          ),
        ],
      ),
    );
  }
}
