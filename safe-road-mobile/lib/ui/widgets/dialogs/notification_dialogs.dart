import 'dart:convert';

import 'package:flutter/material.dart';

import '../../../data/services/notification_service.dart';
import '../../theme/app_theme.dart';

class NotificationDialogs {
  /// Простой стандартный диалог для обычных уведомлений
  static Future<void> showSimpleDialog(
    BuildContext context,
    NotificationEvent evt,
  ) {
    return showDialog<void>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(evt.title.isNotEmpty ? evt.title : 'Уведомление'),
        content: Text(evt.content),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Ок'),
          ),
        ],
      ),
    );
  }

  /// Красивый диалог с наградой за опыт и новый уровень
  static Future<void> showRewardDialog(
    BuildContext context,
    NotificationEvent evt,
  ) {
    // Безопасный парсинг данных
    final payload = _safeDecode(evt.content);

    final int earnedXp = (payload['earnedXp'] ?? 0) as int;
    final int totalXp = (payload['totalXp'] ?? 0) as int;
    final bool levelUp = (payload['levelUp'] ?? false) as bool;
    final int newLevel = (payload['newLevel'] ?? 0) as int;

    return showDialog<void>(
      context: context,
      builder: (context) => Dialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              _buildRewardImage(),
              Text(
                levelUp ? 'Новый уровень!' : 'Поздравляем!',
                style: AppTextStyles.achievementTitle,
                textAlign: TextAlign.center,
              ),
              if (levelUp) ...[
                const SizedBox(height: 8),
                Text(
                  'Вы достигли уровня $newLevel',
                  style: AppTextStyles.bodyLarge,
                ),
              ],
              const SizedBox(height: 8),
              const Text(
                'Получено очков опыта',
                style: AppTextStyles.bodyMedium,
              ),
              const SizedBox(height: 4),
              Text('+$earnedXp', style: AppTextStyles.achievementXp),
              Text('Всего: $totalXp XP', style: AppTextStyles.body14),
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primaryGreen,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  onPressed: () => Navigator.of(context).pop(),
                  child: const Text('Отлично', style: AppTextStyles.buttonText),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  static Map<String, dynamic> _safeDecode(String content) {
    try {
      return jsonDecode(content) as Map<String, dynamic>;
    } catch (_) {
      return {};
    }
  }

  static Widget _buildRewardImage() {
    return Image.asset(
      'assets/images/congrats_600.png',
      /*width: 96,
      height: 96,*/
      errorBuilder: (c, e, s) => const Icon(
        Icons.card_giftcard,
        size: 64,
        color: AppColors.orangeCatAccent,
      ),
    );
  }
}
