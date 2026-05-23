import 'package:flutter/material.dart';

import '../../../data/models/learning/topic.dart';
import '../../../data/models/learning/topic_status.dart';
import '../../theme/app_theme.dart';

class TopicItem extends StatelessWidget {
  final Topic topic;
  final int sectionId;
  final int index;
  final bool rightDirection;

  const TopicItem({
    super.key,
    required this.topic,
    required this.sectionId,
    required this.index,
    required this.rightDirection,
  });

  @override
  Widget build(BuildContext context) {
    final status = topic.status;
    final isLocked = status == TopicStatus.LOCKED;

    // Выбор выравнивания
    final alignments = rightDirection
        ? const [Alignment(-0.5, 0), Alignment.center, Alignment(0.5, 0)]
        : const [Alignment.centerLeft, Alignment.centerRight];
    final align = alignments[index % alignments.length];

    return Align(
      alignment: align,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 18, horizontal: 24),
        child: Column(
          children: [
            _buildCircleButton(context, isLocked),
            const SizedBox(height: 8),
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

  Widget _buildCircleButton(BuildContext context, bool isLocked) {
    final color = isLocked ? AppColors.brownBorder : AppColors.primaryGreen;
    final icon = isLocked
        ? Icons.lock
        : (topic.status == TopicStatus.COMPLETED ? Icons.check : null);

    return GestureDetector(
      onTap: isLocked ? null : () => debugPrint('Open: ${topic.title}'),
      child: Stack(
        alignment: Alignment.center,
        children: [
          Container(
            width: 72,
            height: 72,
            decoration: BoxDecoration(
              color: color,
              shape: BoxShape.circle,
              boxShadow: [
                BoxShadow(
                  color: isLocked
                      ? AppColors.darkBrownText.withValues(alpha: 0.25)
                      : AppColors.white.withValues(alpha: 0.5),
                  blurRadius: 9,
                ),
              ],
            ),
          ),
          if (icon != null)
            Positioned(
              right: 4,
              top: 4,
              child: Icon(
                icon,
                color: isLocked ? AppColors.darkBrownText : AppColors.white,
                size: 24,
              ),
            ),
          Text(topic.orderIndex.toString(), style: AppTheme.topicNumber),
        ],
      ),
    );
  }
}
