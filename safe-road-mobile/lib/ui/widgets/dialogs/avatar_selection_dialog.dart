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
    return AlertDialog(
      backgroundColor: AppColors.white,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
      title: const Text('Выбрать аватар', textAlign: TextAlign.center),
      content: _buildContent(context),
    );
  }

  Widget _buildContent(BuildContext context) {
    if (_isLoading) {
      return const SizedBox(
        height: 160,
        child: Center(child: CircularProgressIndicator()),
      );
    }
    if (_errorMessage != null || _avatars == null) {
      return SizedBox(
        height: 160,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_errorMessage ?? 'Ошибка', style: AppTextStyles.bodyMedium),
            const SizedBox(height: 10),
            ElevatedButton(
              onPressed: () {
                setState(() {
                  _isLoading = true;
                  _errorMessage = null;
                });
                _loadAvatars();
              },
              child: const Text('Повторить'),
            ),
          ],
        ),
      );
    }

    // Читаем данные из провайдера
    final profileProvider = context.read<GameProfileProvider>();
    final activeAvatar = _selectedAvatar;
    final isSelectedAvailable =
        activeAvatar != null && activeAvatar.isAvailable;

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        SizedBox(
          height: 160,
          width: MediaQuery.of(context).size.width * 0.8,
          child: ListView.builder(
            scrollDirection: Axis.horizontal,
            itemCount: _avatars!.length,
            itemBuilder: (context, index) {
              final avatar = _avatars![index];
              final isSelected = _selectedAvatarId == avatar.id;
              final isAvailable = avatar.isAvailable;

              return GestureDetector(
                onTap: () => setState(() => _selectedAvatarId = avatar.id),
                child: _buildAvatarItem(avatar, isSelected, isAvailable),
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
              child: const Text('Отмена', style: AppTextStyles.bodyMedium),
            ),
            const SizedBox(width: 10),
            ElevatedButton(
              onPressed: isSelectedAvailable
                  ? () async {
                      // Сами вызываем обновление в провайдере!
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
        ),
      ],
    );
  }

  Widget _buildAvatarItem(Avatar avatar, bool isSelected, bool isAvailable) {
    return Stack(
      alignment: Alignment.center,
      children: [
        Container(
          margin: const EdgeInsets.symmetric(horizontal: 5),
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
              radius: 50,
              backgroundColor: AppColors.lightBlueBackground,
              child: ClipOval(
                child: SecureNetworkImage(
                  imageUrl: avatar.url,
                  fit: BoxFit.cover,
                  width: 100,
                  height: 100,
                ),
              ),
            ),
          ),
        ),
        if (!isAvailable)
          const Icon(Icons.lock, size: 40, color: Colors.black54),
      ],
    );
  }
}
