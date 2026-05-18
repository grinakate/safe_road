import 'package:flutter/material.dart';
import 'package:safe_road/core/avatar_manager.dart';
import 'package:safe_road/models/avatar_models.dart';
import 'package:safe_road/widgets/secure_network_image.dart';

import '../theme.dart';

class AvatarSelectionDialog extends StatefulWidget {
  final int currentAvatarId; // Текущая аватарка пользователя
  final int currentUserLevel;
  final Function(int newAvatarId) onAvatarSelected;

  const AvatarSelectionDialog({
    super.key,
    required this.currentAvatarId,
    required this.currentUserLevel,
    required this.onAvatarSelected,
  });

  @override
  State<AvatarSelectionDialog> createState() => _AvatarSelectionDialogState();
}

class _AvatarSelectionDialogState extends State<AvatarSelectionDialog> {
  late List<AvatarModel> _availableAvatars;
  int? _selectedAvatarId;
  bool? _selectedAvailable;

  @override
  void initState() {
    super.initState();
    _availableAvatars = AvatarManager.getAvailableAvatarItems();
    _selectedAvatarId = widget.currentAvatarId;
    _selectedAvailable = true;
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      backgroundColor: AppColors.white,
      surfaceTintColor: AppColors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      title: Text(
        'Выбрать аватар',
        textAlign: TextAlign.center,
        style: AppTextStyles.titleLarge.copyWith(fontSize: 18),
      ),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          SizedBox(
            height: MediaQuery.of(context).size.height * 0.3,
            width: MediaQuery.of(context).size.width * 0.8,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemCount: _availableAvatars.length,
              itemBuilder: (context, index) {
                final avatar = _availableAvatars[index];
                final isSelected = _selectedAvatarId == avatar.id;
                final isAvailable = widget.currentUserLevel >= avatar.minLevel;

                return GestureDetector(
                  onTap: () {
                    setState(() {
                      _selectedAvatarId = avatar.id;
                      _selectedAvailable = isAvailable;
                    });
                  },
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      Opacity(
                        opacity: isAvailable ? 1.0 : 0.4,
                        child: Container(
                          margin: const EdgeInsets.symmetric(horizontal: 5),
                          decoration: BoxDecoration(
                            shape: BoxShape.circle,
                            border: Border.all(
                              color: isSelected
                                  ? AppColors.primaryGreen
                                  : AppColors.white.withAlpha(0),
                              width: 3,
                            ),
                          ),
                          child: CircleAvatar(
                            radius: 70,
                            backgroundColor: AppColors.lightBlueBackground,
                            child: SecureNetworkImage(
                              imageUrl: avatar.url,
                              fit: BoxFit.cover,
                            ),
                          ),
                        ),
                      ),
                      if (!isAvailable)
                        Icon(
                          Icons.lock,
                          size: 40,
                          color: AppColors.darkBrownText,
                        ),
                    ],
                  ),
                );
              },
            ),
          ),
          const SizedBox(height: 20),
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              TextButton(
                onPressed: () => Navigator.of(context).pop(),
                // Просто закрываем
                child: const Text(
                  'Отмена',
                  style: AppTextStyles.bodyMedium,
                ),
              ),
              const SizedBox(width: 10),
              SizedBox(
                width: 150,
                child: ElevatedButton(
                  onPressed: _selectedAvailable == null || _selectedAvailable!
                      ? () {
                          widget.onAvatarSelected(_selectedAvatarId!);
                          Navigator.of(context).pop();
                        } // Кнопка неактивна, если ничего не выбрано или выбрана текущая
                      : null,
                  child: Text(
                    _selectedAvailable!
                        ? 'Выбрать'
                        : '${AvatarManager.getAvailableAvatarMap()[_selectedAvatarId!]?.minLevel.toString()} Уровень',
                    style: AppTextStyles.buttonText,
                    maxLines: 1,
                    textAlign: TextAlign.center,
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
