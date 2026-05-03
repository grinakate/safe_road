import 'package:flutter/material.dart';
import 'package:safe_road/core/constants.dart';

import '../models/user_profile.dart';

class RoadHeader extends StatelessWidget {
  final UserProfile profile; // Принимаем профиль

  const RoadHeader({super.key, required this.profile});

  @override
  Widget build(BuildContext context) {
    return Container(
      color: AppConstants.skyColor,
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
      child: Column(
        children: [
          Row(
            children: [
              // TODO: добавить логику avatarId
              CircleAvatar(
                radius: 26,
                backgroundColor: AppConstants.bgPrimaryColor,
                child: Image.asset("assets/images/cat.png"),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
/*                    Text(
                      '${profile.xpToNextLevel} очков до следующего уровня',
                      style: const TextStyle(
                        fontSize: 13,
                        fontFamily: 'Nunito',
                        color: Colors.brown,
                      ),
                    ),*/
                    //const SizedBox(height: 6),
                    ClipRRect(
                      borderRadius: BorderRadius.circular(10),
                      child: LinearProgressIndicator(
                        value: profile.xpProgress,
                        minHeight: 16,
                        color: AppConstants.greenTestColor,
                        backgroundColor: AppConstants.lightGreenColor,
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
                  // color: AppConstants.borderColor,
                  color: Colors.brown,
                  fontWeight: FontWeight.bold,
                  fontSize: 14,
                  fontFamily: 'Nunito',
                ),
              ),
              Text(
                '${profile.completedLessons}/${profile.totalLessons} Уроков пройдено',
                style: const TextStyle(
                  color: Colors.brown,
                  fontSize: 13,
                  fontFamily: 'Nunito',
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
