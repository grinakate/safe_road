import 'package:flutter/material.dart';

class RoadHeader extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.9),
        borderRadius: const BorderRadius.vertical(bottom: Radius.circular(20)),
      ),
      child: Column(
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 30,
                backgroundColor: Colors.orange[100],
                child: const Icon(Icons.pets, size: 30, color: Colors.orange),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text("150 очков до следующего уровня", style: TextStyle(fontSize: 12)),
                    const SizedBox(height: 4),
                    LinearProgressIndicator(
                      value: 0.4, // Процент прогресса
                      backgroundColor: Colors.grey[300],
                      color: Colors.green,
                      minHeight: 10,
                      borderRadius: BorderRadius.circular(5),
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          const Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text("Уровень 1: 0 очков", style: TextStyle(fontWeight: FontWeight.bold)),
              Text("0/71 Уроков пройдено"),
            ],
          )
        ],
      ),
    );
  }
}
