import 'package:flutter/material.dart';

import '../models/user_profile.dart';

class RoadHeader extends StatelessWidget {
  final UserProfile profile; // Принимаем профиль

  const RoadHeader({super.key, required this.profile});

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.white,
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
      child: Column(
        children: [
          Row(
            children: [
              // Аватарка (можно добавить логику avatarId)
              CircleAvatar(
                radius: 26,
                backgroundColor: Colors.orange[100],
                child: const Icon(Icons.pets, color: Colors.orange, size: 30),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '${profile.xpToNextLevel} очков до уровня ${profile.level.number + 1}',
                      style: const TextStyle(
                        fontSize: 13,
                        color: Colors.black54,
                      ),
                    ),
                    const SizedBox(height: 6),
                    ClipRRect(
                      borderRadius: BorderRadius.circular(10),
                      child: LinearProgressIndicator(
                        value: profile.xpProgress,
                        minHeight: 12,
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
            children: [
              Text(
                'Уровень ${profile.level.number}: ${profile.currentXp} очков',
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 14,
                ),
              ),
              Text(
                '${profile.completedLessons}/${profile.totalLessons} Уроков пройдено',
                style: const TextStyle(color: Colors.black45, fontSize: 13),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
