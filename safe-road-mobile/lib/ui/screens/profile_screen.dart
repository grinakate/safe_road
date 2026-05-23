import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../data/models/gamification/achievement.dart';
import '../../data/models/gamification/game_profile.dart';
import '../../logic/providers/game_profile_provider.dart';
import '../theme/app_theme.dart';
import '../widgets/dialogs/achievement_info_dialog.dart';
import '../widgets/dialogs/avatar_selection_dialog.dart';
import '../widgets/common/secure_network_image.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final gp = context.read<GameProfileProvider>();
      gp.loadProfile();
      gp.loadAchievements();
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
    return Consumer<GameProfileProvider>(
      builder: (context, gp, child) {
        if (gp.profile == null) {
          return const Center(child: CircularProgressIndicator());
        }

        final user = gp.profile!;
        final UserAvatar userAvatar = user.avatar;

        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 10),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Expanded(child: _buildStatItem("Уровень", user.level.toString())),
              Expanded(
                child: GestureDetector(
                  onTap: () {
                    // Просто вызываем диалог без параметров!
                    showDialog(
                      context: context,
                      builder: (context) => const AvatarSelectionDialog(),
                    );
                  },
                  child: CircleAvatar(
                    radius: 60,
                    backgroundColor: AppColors.lightBlueBackground,
                    child: ClipOval(
                      child: SecureNetworkImage(
                        imageUrl: userAvatar.url,
                        fit: BoxFit.cover,
                        width: 120,
                        height: 120,
                      ),
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
    return const Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        _TabButton("Значки", isActive: true),
        _TabButton("Статистика"),
        _TabButton("Рейтинг"),
      ],
    );
  }

  // Сетка достижений (использует провайдер с кэшем и фоновым обновлением)
  Widget _buildAchievementsGrid() {
    return Consumer<GameProfileProvider>(
      builder: (context, gp, _) {
        if (gp.loadingAchievements && gp.achievements.isEmpty) {
          return const Center(child: CircularProgressIndicator());
        }

        final achievements = gp.achievements;

        if (achievements.isEmpty) {
          return const Center(child: Text('Нет достижений'));
        }

        return Expanded(
          child: LayoutBuilder(
            builder: (context, constraints) {
              const double listPadding = 20.0;
              const double itemMargin = 4.0;
              double availableWidth = constraints.maxWidth - listPadding;
              double itemWidth = (availableWidth - 2 * itemMargin) / 3;

              double fontSize = itemWidth * 0.12;
              double textHeight = fontSize * 1.2 * 2.5;

              final rows = _chunkAchievements(achievements);
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
                              imageSize: itemWidth,
                            ),
                          );
                        },
                        child: Container(
                          width: itemWidth,
                          margin: const EdgeInsets.symmetric(
                            horizontal: itemMargin,
                          ),
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Opacity(
                                opacity: item.isUnlocked ? 1.0 : 0.4,
                                child: SecureNetworkImage(
                                  imageUrl: item.iconUrl,
                                  width: itemWidth,
                                  height: itemWidth,
                                  fit: BoxFit.cover,
                                ),
                              ),
                              const SizedBox(height: 8),
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

  // Логика разбивки на ряды
  List<List<Achievement>> _chunkAchievements(List<Achievement> data) {
    List<List<Achievement>> rows = [];
    int i = 0;
    bool isThree = true;

    while (i < data.length) {
      int count = isThree ? 3 : 2;
      rows.add(
        data.sublist(i, (i + count > data.length) ? data.length : i + count),
      );
      i += count;
      isThree = !isThree;
    }
    return rows;
  }
}

class _TabButton extends StatelessWidget {
  final String text;
  final bool isActive;

  const _TabButton(this.text, {this.isActive = false});

  @override
  Widget build(BuildContext context) {
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
}
