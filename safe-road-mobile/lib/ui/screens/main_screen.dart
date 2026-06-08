import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:safe_road/ui/screens/profile/profile_screen.dart';
import 'package:safe_road/ui/screens/learning/theory_tree_screen.dart';

import '../../logic/providers/game_profile_provider.dart';
import '../theme/app_theme.dart';
import 'leaderboard/leaderboard_screen.dart';
import 'learning/roadmap_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    // 1. Подписываемся на изменения профиля
    final profileProvider = context.watch<GameProfileProvider>();
    final isParticipant = profileProvider.profile?.leaderboardEnabled ?? false;

    // 2. Динамически формируем список экранов
    final List<Widget> screens = [
      const RoadMapScreen(),
      const TheoryTreeScreen(),
      if (isParticipant) const LeaderboardScreen(),
      const ProfileScreen(),
    ];

    // 3. Динамически формируем элементы меню
    final List<BottomNavigationBarItem> navItems = [
      BottomNavigationBarItem(
        icon: Image.asset("assets/icons/road.png", height: 24),
        label: 'Дорога',
      ),
      BottomNavigationBarItem(
        icon: Image.asset("assets/icons/book.png", height: 24),
        label: 'Теория',
      ),
      if (isParticipant)
        BottomNavigationBarItem(
          icon: Image.asset("assets/icons/leader_board.png", height: 24),
          label: 'Рейтинг',
        ),
      BottomNavigationBarItem(
        icon: Image.asset("assets/icons/profile.png", height: 24),
        label: 'Профиль',
      ),
    ];

    // 4. Защита от выхода за границы массива
    // Если мы были на вкладке "Рейтинг" (индекс 2), а она исчезла,
    // индекс станет невалидным. Сбрасываем его на 0.
    if (_selectedIndex >= screens.length) {
      _selectedIndex = 0;
    }

    return Scaffold(
      body: IndexedStack(index: _selectedIndex, children: screens),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (index) => setState(() => _selectedIndex = index),
        selectedItemColor: AppColors.darkBrownText,
        unselectedItemColor: AppColors.brownText.withValues(alpha: 0.6),
        type: BottomNavigationBarType.fixed,
        items: navItems,
      ),
    );
  }
}
