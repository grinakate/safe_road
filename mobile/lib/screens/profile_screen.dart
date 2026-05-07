import 'package:flutter/material.dart';
import 'package:safe_road/core/constants.dart';

import '../core/avatar_manager.dart';
import '../core/service_locator.dart';
import '../models/achievement.dart';
import '../models/user_profile.dart';
import '../services/user_service.dart';
import '../widgets/achievement_info_dialog.dart';
import '../widgets/avatar_selection_dialog.dart';
import '../widgets/secure_network_image.dart';
import '../theme.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  late Future<UserProfile> _profileFuture;
  late Future<void> _avatarsInitFuture;
  UserProfile? _currentUserProfile;

  @override
  void initState() {
    super.initState();
    _avatarsInitFuture = getIt<UserService>().getAvailableAvatars().then((
      avatarInfoList,
    ) {
      AvatarManager.initialize(avatarInfoList);
    });

    _profileFuture = getIt<UserService>().getProfile().then((profile) {
      _currentUserProfile = profile;
      return profile;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      /*      appBar: AppBar(
        title: const Text('Профиль', style: TextStyle()),
        centerTitle: true,
        actions: [
          IconButton(icon: const Icon(Icons.settings), onPressed: () {}),
        ],
      ),*/
      body: SafeArea(
        child: FutureBuilder<void>(
          future: Future.wait([_avatarsInitFuture, _profileFuture]),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const Center(child: CircularProgressIndicator());
            }
            if (snapshot.hasError) {
              return Center(child: Text("Ошибка: ${snapshot.error}"));
            }

            final user = _currentUserProfile!;
            return Column(
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                const SizedBox(height: 30),
                _buildHeader(user),
                const SizedBox(height: 10),
                _buildTabs(),
                const SizedBox(height: 10),
                Expanded(child: _buildAchievementsGrid(user.achievements)),
              ],
            );
          },
        ),
      ),
    );
  }

  // Верхняя часть: Уровень, Аватар, Очки
  Widget _buildHeader(UserProfile user) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Expanded(
            child: _buildStatItem("Уровень", user.level.number.toString()),
          ),
          Expanded(
            child: GestureDetector(
              onTap: () {
                _showAvatarSelectionDialog(user.avatarId, user.level.number);
              },
              child: CircleAvatar(
                radius: 60,
                backgroundColor: Colors.blue.withAlpha(26), // Replaced withOpacity with withAlpha
                child: SecureNetworkImage(
                  imageUrl: AvatarManager.getAvatarItem(user.avatarId).url,
                  fit: BoxFit.cover,
                ),
              ),
            ),
          ),
          Expanded(child: _buildStatItem("Очки", user.currentXp.toString())),
        ],
      ),
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
          if (_currentUserProfile!.avatarId == newAvatarId) {
            return;
          }
          // 1. Отправляем на бэкенд
          var newAvatar = await getIt<UserService>().updateAvatar(newAvatarId);
          // 2. Обновляем UI
          setState(() {
            _currentUserProfile = _currentUserProfile!.copyWith(
              avatarId: newAvatar.id,
            );
          });
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
        Text(
          label,
          style: AppTheme.subHeaderTextStyle.copyWith(fontSize: 24),
        ),
        Text(
          value,
          style: AppTheme.headerTextStyle.copyWith(fontSize: 24),
        ),
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
        color: isActive ? Colors.teal.withAlpha(51) : Colors.transparent, // Replaced withOpacity with withAlpha
        borderRadius: BorderRadius.circular(15),
      ),
      child: Text(
        text,
        style: AppTheme.body14.copyWith(
          fontSize: 18,
          color: isActive ? Colors.teal[800] : Colors.black54,
          fontWeight: isActive ? FontWeight.bold : FontWeight.normal,
        ),
      ),
    );
  }

  // Сетка достижений
  Widget _buildAchievementsGrid(List<Achievement> achievements) {
    final rows = chunkAchievements(achievements);

    return LayoutBuilder(
      builder: (context, constraints) {
        double listPadding = 20.0;
        double itemMargin = 4.0;
        double availableWidth = constraints.maxWidth - listPadding;
        double itemWidth = availableWidth / 3.3;

        double fontSize = itemWidth * 0.12;
        double textHeight = fontSize * 1.2 * 2.5;

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
                          child: SecureNetworkImage(imageUrl: item.iconUrl),
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
                                  ? AppConstants.borderColor
                                  : Colors.brown,
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
