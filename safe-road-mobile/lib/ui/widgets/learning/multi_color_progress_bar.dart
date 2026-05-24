import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../../theme/app_colors.dart';

class MultiColorProgressBar extends StatelessWidget {
  final int correct;
  final int wrong;
  final int notShown;

  const MultiColorProgressBar({
    super.key,
    required this.correct,
    required this.wrong,
    required this.notShown,
  });

  @override
  Widget build(BuildContext context) {
    if (correct + wrong + notShown == 0) {
      return Container(
        height: 8,
        color: AppColors.greyIcon.withValues(alpha: 0.2),
      );
    }

    return ClipRRect(
      borderRadius: BorderRadius.circular(4),
      child: SizedBox(
        height: 8,
        child: Row(
          children: [
            if (correct > 0)
              Expanded(
                flex: correct,
                child: Container(color: AppColors.primaryGreen),
              ),
            if (wrong > 0)
              Expanded(
                flex: wrong,
                child: Container(
                  color: AppColors.errorRed.withValues(alpha: 0.7),
                ),
              ),
            if (notShown > 0)
              Expanded(
                flex: notShown,
                child: Container(
                  color: AppColors.greyIcon.withValues(alpha: 0.2),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
