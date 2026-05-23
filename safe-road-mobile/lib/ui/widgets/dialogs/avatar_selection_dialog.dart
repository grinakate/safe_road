import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/core/service_locator.dart';
import 'package:safe_road/data/services/game_profile_service.dart';
import 'package:safe_road/ui/widgets/common/secure_network_image.dart';

import '../../../data/models/gamification/avatar.dart';
import '../../../logic/providers/game_profile_provider.dart';
import '../../theme/app_theme.dart';

class AvatarSelectionDialog extends StatefulWidget {
  const AvatarSelectionDialog({super.key});

  @override
  State<AvatarSelectionDialog> createState() => _AvatarSelectionDialogState();
}

class _AvatarSelectionDialogState extends State<AvatarSelectionDialog> {
  final GameProfileService _profileService = getIt<GameProfileService>();

  List<Avatar>? _avatars;
  int? _selectedAvatarId;
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadAvatars();

    // Получаем текущий аватар из провайдера при инициализации
    final profileProvider = context.read<GameProfileProvider>();
    _selectedAvatarId = profileProvider.profile?.avatar.id;
  }

  Future<void> _loadAvatars() async {
    try {
      final avatars = await _profileService.getAvailableAvatars();
      if (mounted) {
        setState(() {
          _avatars = avatars;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _errorMessage = 'Не удалось загрузить аватары';
          _isLoading = false;
        });
      }
    }
  }

  Avatar? get _selectedAvatar {
    if (_avatars == null || _avatars!.isEmpty) return null;
    return _avatars!.firstWhere(
      (a) => a.id == _selectedAvatarId,
      orElse: () => _avatars!.first,
    );
  }

  @override
  Widget build(BuildContext context) {
    // Вычисляем статус кнопки "Выбрать" заранее
    final activeAvatar = _selectedAvatar;
    final bool isSelectedAvailable = activeAvatar?.isAvailable ?? false;
    final profileProvider = context.read<GameProfileProvider>();

    return AlertDialog(
      backgroundColor: AppColors.white,
      surfaceTintColor: Colors.transparent,
      // Чтобы не было лишних оттенков
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      title: const Text('Выбрать аватар', textAlign: TextAlign.center),

      // ГЛАВНОЕ: Только список аватаров в контенте
      content: _buildContent(context),

      // ГЛАВНОЕ: Кнопки выносим в actions
      actions: [
        // TextButton(
        //   onPressed: () => Navigator.of(context).pop(),
        //   child: const Text(
        //     'Отмена',
        //     style: TextStyle(color: AppColors.brownText),
        //   ),
        // ),
        ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: isSelectedAvailable
                ? AppColors.primaryGreen
                : AppColors.greyBorder,
            foregroundColor: Colors.white,
          ),
          onPressed: isSelectedAvailable
              ? () async {
                  if (_selectedAvatarId != null) {
                    await profileProvider.updateAvatar(_selectedAvatarId!);
                    if (context.mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(
                          content: Text('Аватар успешно обновлен!'),
                        ),
                      );
                      Navigator.of(context).pop();
                    }
                  }
                }
              : null,
          child: Text(
            isSelectedAvailable
                ? 'Выбрать'
                : 'Доступно с ${activeAvatar?.minLevel ?? 0} ур.',
          ),
        ),
      ],
    );
  }

  Widget _buildContent(BuildContext context) {
    if (_isLoading) {
      return const SizedBox(
        height: 120,
        child: Center(child: CircularProgressIndicator()),
      );
    }
    if (_errorMessage != null || _avatars == null) {
      return Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(_errorMessage ?? 'Ошибка', style: AppTextStyles.bodyMedium),
          TextButton(onPressed: _loadAvatars, child: const Text('Повторить')),
        ],
      );
    }

    return Column(
      mainAxisSize: MainAxisSize.min, // Чтобы диалог сжимался по контенту
      children: [
        const Text(
          'Листайте вправо, чтобы увидеть всех',
          style: TextStyle(fontSize: 10, color: Colors.grey),
        ),
        const SizedBox(height: 10),
        SizedBox(
          height: 120, // Фиксированная высота для горизонтального списка
          width: double.maxFinite, // Занимаем всю ширину диалога
          child: ListView.builder(
            scrollDirection: Axis.horizontal,
            shrinkWrap: true,
            itemCount: _avatars!.length,
            itemBuilder: (context, index) {
              final avatar = _avatars![index];
              final isSelected = _selectedAvatarId == avatar.id;
              final isAvailable = avatar.isAvailable;

              return GestureDetector(
                behavior: HitTestBehavior.opaque, // Чтобы кликалась вся область
                onTap: () => setState(() => _selectedAvatarId = avatar.id),
                child: _buildAvatarItem(avatar, isSelected, isAvailable),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildAvatarItem(Avatar avatar, bool isSelected, bool isAvailable) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      child: Stack(
        alignment: Alignment.center,
        children: [
          Container(
            padding: const EdgeInsets.all(3),
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              border: Border.all(
                color: isSelected ? AppColors.primaryGreen : Colors.transparent,
                width: 3,
              ),
            ),
            child: Opacity(
              opacity: isAvailable ? 1.0 : 0.4,
              child: CircleAvatar(
                radius: 45,
                backgroundColor: AppColors.lightBlueBackground,
                child: ClipOval(
                  child: SecureNetworkImage(
                    imageUrl: avatar.url,
                    fit: BoxFit.cover,
                    width: 90,
                    height: 90,
                  ),
                ),
              ),
            ),
          ),
          if (!isAvailable)
            Container(
              decoration: BoxDecoration(
                color: Colors.black26,
                shape: BoxShape.circle,
              ),
              padding: const EdgeInsets.all(8),
              child: const Icon(Icons.lock, size: 30, color: Colors.white),
            ),
        ],
      ),
    );
  }
}
