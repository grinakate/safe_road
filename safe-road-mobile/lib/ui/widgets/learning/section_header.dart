import 'package:flutter/material.dart';

import '../../../data/models/learning/section.dart';
import '../../theme/app_theme.dart';

class SectionHeader extends StatelessWidget {
  final Section section;

  const SectionHeader({super.key, required this.section});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(vertical: 20, horizontal: 56),
      padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 8),
      decoration: BoxDecoration(
        color: AppColors.lightGreenBackground,
        borderRadius: BorderRadius.circular(20),
        //border: Border.all(color: AppColors.brown),
        boxShadow: [
          BoxShadow(
            color: AppColors.brown.withValues(alpha: 0.3),
            blurRadius: 4,
            offset: const Offset(0, 0),
          ),
        ],
      ),
      child: Row(
        children: [
          Expanded(
            child: Text(
              '${section.title} - ${section.progressPercent}%',
              textAlign: TextAlign.center,
              style: AppTheme.sectionHeaderText,
            ),
          ),
        ],
      ),
    );
  }
}
