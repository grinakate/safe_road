import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/logic/providers/learning_provider.dart';
import 'package:safe_road/ui/screens/profile/settings_screen.dart';
import 'package:safe_road/ui/widgets/profile/profile_header.dart';

import '../../../data/models/gamification/achievement.dart';
import '../../../logic/providers/game_profile_provider.dart';
import '../../theme/app_theme.dart';
import '../../widgets/common/secure_network_image.dart';
import '../../widgets/dialogs/achievement_info_dialog.dart';
import '../../widgets/learning/multi_color_progress_bar.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen>
    with TickerProviderStateMixin {
  late int _currentIndex = 0;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final gp = context.read<GameProfileProvider>();
      gp.loadProfile();
      gp.loadAchievements();

      final learningProvider = context.read<LearningProvider>();
      learningProvider.loadSectionStatistics();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.blueBackground,
      appBar: AppBar(
        title: Consumer<GameProfileProvider>(
          builder: (context, gp, _) => Text(
            gp.profile?.nickname ?? 'Профиль',
            style: AppTheme.appBarTitle,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings, color: AppColors.brownText),
            onPressed: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const SettingsScreen()),
            ),
          ),
        ],
        backgroundColor: AppColors.white,
        elevation: 0,
      ),
      body: SafeArea(
        child: Column(
          children: [
            // Верхняя часть: Уровень, Аватар, Очки
            const ProfileHeader(),
            Padding(
              padding: const EdgeInsets.symmetric(
                vertical: 8.0,
                horizontal: 16,
              ),
              child: Row(
                children: [
                  Expanded(child: _buildChoiceChip(0, 'Значки')),
                  const SizedBox(width: 10),
                  Expanded(child: _buildChoiceChip(1, 'Статистика')),
                ],
              ),
            ),
            Expanded(
              child: IndexedStack(
                index: _currentIndex,
                children: [_buildAchievementsGrid(), _buildStatisticsContent()],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // Метод для создания однотипных кнопок
  Widget _buildChoiceChip(int index, String label) {
    return ChoiceChip(
      label: SizedBox(
        width: double.infinity,
        child: Center(child: Text(label)),
      ),
      selected: _currentIndex == index,
      onSelected: (bool selected) {
        if (selected) setState(() => _currentIndex = index);
      },
      showCheckmark: false,
      selectedColor: AppColors.primaryGreen,
      backgroundColor: AppColors.white,
      labelStyle: AppTextStyles.buttonText.copyWith(
        color: _currentIndex == index
            ? AppColors.white
            : AppColors.primaryGreen,
      ),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
    );
  }

  Widget _buildStatisticsContent() {
    return Consumer<LearningProvider>(
      builder: (context, lp, _) {
        if (lp.loadingSectionStat && lp.sectionStats.isEmpty) {
          return const Center(child: CircularProgressIndicator());
        }

        final statistics = lp.sectionStats;

        return ListView.separated(
          padding: const EdgeInsets.all(16),
          itemCount: statistics.length,
          separatorBuilder: (_, _) => const SizedBox(height: 8),
          itemBuilder: (context, index) {
            final section = statistics[index];
            final progress = section.totalTopics > 0
                ? section.completedTopics / section.totalTopics
                : 0.0;
            return Card(
              elevation: 2,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              child: ExpansionTile(
                initiallyExpanded: true,
                collapsedShape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                tilePadding: const EdgeInsets.symmetric(horizontal: 16),
                title: Text(
                  section.title,
                  style: AppTextStyles.headlineLarge.copyWith(fontSize: 18),
                ),
                children: section.topicStatistics.map((topic) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Expanded(child: Text(topic.title, style: AppTheme.body14)),
                            Text('${topic.correctAnswers}/${topic.totalQuestions}',
                                style: AppTheme.body14),
                          ],
                        ),
                        const SizedBox(height: 8),
                        MultiColorProgressBar(
                          correct: topic.correctAnswers,
                          wrong: topic.wrongAnswers,
                          notShown: topic.notShown,
                        ),
                        const SizedBox(height: 8),
                      ],
                    ),
                  );
                }).toList(),
              ),
            );
          },
        );
      },
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

        return LayoutBuilder(
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
