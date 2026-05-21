import 'package:flutter/material.dart';

import '../core/avatar_manager.dart';
import '../core/service_locator.dart';
import '../models/achievement.dart';
import '../services/game_profile_service.dart';
import 'package:provider/provider.dart';
import '../providers/game_profile_provider.dart';
import '../theme.dart';
import '../widgets/achievement_info_dialog.dart';
import '../widgets/avatar_selection_dialog.dart';
import '../widgets/secure_network_image.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {

  @override
  void initState() {
    super.initState();
    // Загружаем профиль и аватары через провайдер
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final gp = context.read<GameProfileProvider>();
      gp.loadAvatars().then((_) => AvatarManager.initialize(gp.availableAvatars));
      gp.loadProfile();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            const SizedBox(height: 30),
            _buildHeader(),
            const SizedBox(height: 10),
            _buildTabs(),
            const SizedBox(height: 10),
            _buildAchievementsGrid(),
          ],
        ),
      ),
    );
  }

  // Верхняя часть: Уровень, Аватар, Очки
  Widget _buildHeader() {
    return Consumer<GameProfileProvider>(builder: (context, gp, child) {
      if (gp.profile == null) {
        return const Center(child: CircularProgressIndicator());
      }
      final user = gp.profile!;
        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 10),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Expanded(
                child: _buildStatItem("Уровень", user.level.toString()),
              ),
              Expanded(
                child: GestureDetector(
                  onTap: () {
                    _showAvatarSelectionDialog(
                      user.avatarId,
                      user.level,
                    );
                  },
                  child: CircleAvatar(
                    radius: 60,
                    backgroundColor: AppColors.lightBlueBackground,
                    child: SecureNetworkImage(
                      imageUrl: AvatarManager.getAvatarItem(user.avatarId).url,
                      fit: BoxFit.cover,
                    ),
                  ),
                ),
              ),
              Expanded(
                child: _buildStatItem("Очки", user.currentXp.toString()),
              ),
            ],
          ),
        );
      },
    );
  }

  // Новый метод для показа диалога выбора аватара
  void _showAvatarSelectionDialog(int currentAvatarId, int currentUserLevel) {
    showDialog(
      context: context,
      builder: (context) => AvatarSelectionDialog(
        currentAvatarId: currentAvatarId,
        currentUserLevel: currentUserLevel,
        onAvatarSelected: (newAvatarId) async {
          final gp = context.read<GameProfileProvider>();
          await gp.updateAvatar(newAvatarId);
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Аватар успешно обновлен!')),
          );
        },
      ),
    );
  }

  Widget _buildStatItem(String label, String value) {
    return Column(
      children: [
        Text(label, style: AppTextStyles.bodyLarge.copyWith(fontSize: 24)),
        Text(value, style: AppTextStyles.headlineLarge.copyWith(fontSize: 24)),
      ],
    );
  }

  // Переключатель вкладок
  Widget _buildTabs() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        _tabButton("Значки", isActive: true),
        _tabButton("Статистика"),
        _tabButton("Рейтинг"),
      ],
    );
  }

  Widget _tabButton(String text, {bool isActive = false}) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
      decoration: BoxDecoration(
        color: isActive ? AppColors.lightGreenBackground : Colors.transparent,
        borderRadius: BorderRadius.circular(15),
      ),
      child: Text(
        text,
        style: AppTextStyles.bodyLarge.copyWith(
          fontSize: 18,
          color: isActive ? AppColors.primaryGreen : AppColors.brownText,
          fontWeight: isActive ? FontWeight.bold : FontWeight.normal,
        ),
      ),
    );
  }

  // Сетка достижений
  Widget _buildAchievementsGrid() {
    return FutureBuilder<List<Achievement>>(
      future: getIt<GameProfileService>().getAchievements(),
      builder: (context, asyncSnapshot) {
        if (asyncSnapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        if (asyncSnapshot.hasError) {
          return Center(child: Text("Ошибка: ${asyncSnapshot.error}"));
        }
        final achievements = asyncSnapshot.data ?? [];
        return Expanded(
          child: LayoutBuilder(
            builder: (context, constraints) {
              double listPadding = 20.0;
              double itemMargin = 4.0;
              double availableWidth = constraints.maxWidth - listPadding;
              double itemWidth = availableWidth / 3.3;

              double fontSize = itemWidth * 0.12;
              double textHeight = fontSize * 1.2 * 2.5;

              final rows = chunkAchievements(achievements);
              return ListView.builder(
                itemCount: rows.length,
                itemBuilder: (context, rowIndex) {
                  return Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: rows[rowIndex].map((item) {
                      return GestureDetector(
                        onTap: () {
                          showDialog(
                            context: context,
                            builder: (context) => AchievementInfoDialog(
                              achievement: item,
                              imageSize: itemWidth * 1.5,
                            ),
                          );
                        },
                        child: Container(
                          width: itemWidth,
                          //color: Colors.red.withOpacity(0.1), // для отладки границ
                          margin: EdgeInsets.symmetric(horizontal: itemMargin),
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Opacity(
                                opacity: item.isUnlocked ? 1.0 : 0.4,
                                child: SecureNetworkImage(
                                  imageUrl: item.iconUrl,
                                ),
                              ),
                              const SizedBox(height: 8),
                              // --- ТЕКСТ ---
                              SizedBox(
                                height: textHeight,
                                child: Text(
                                  item.title,
                                  textAlign: TextAlign.center,
                                  maxLines: 2,
                                  overflow: TextOverflow.ellipsis,
                                  style: AppTheme.topicName.copyWith(
                                    fontSize: fontSize,
                                    fontWeight: FontWeight.w600,
                                    height: 1.0,
                                    color: item.isUnlocked
                                        ? AppColors.darkBrownText
                                        : AppColors.brownText,
                                  ),
                                ),
                              ),
                            ],
                          ),
                        ),
                      );
                    }).toList(),
                  );
                },
              );
            },
          ),
        );
      },
    );
  }

  List<List<Achievement>> chunkAchievements(List<Achievement> data) {
    List<List<Achievement>> rows = [];
    int i = 0;
    bool isThree = true; // Флаг: сейчас ряд из 3-х или из 2-х элементов

    while (i < data.length) {
      int count = isThree ? 3 : 2;
      // Берем подсписок, но не больше, чем осталось элементов
      rows.add(
        data.sublist(i, (i + count > data.length) ? data.length : i + count),
      );
      i += count;
      isThree = !isThree; // Меняем флаг для следующего ряда
    }
    return rows;
  }
}
