import 'package:flutter/material.dart';
import 'package:safe_road/core/constants.dart';
import '../models/section.dart';
import '../theme.dart';

class SectionHeader extends StatelessWidget {
  final Section section;

  const SectionHeader({super.key, required this.section});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(vertical: 20, horizontal: 60),
      padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 16),
      decoration: BoxDecoration(
        color: AppConstants.greenTestColor,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.18),
            blurRadius: 8,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Row(
        children: [
          Expanded(
            child: Text(
              '${section.name} - ${section.progressPercent}%',
              textAlign: TextAlign.center,
              style: AppTheme.sectionHeaderText,
            ),
          )
        ],
      ),
    );
  }
}
