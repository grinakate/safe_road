import 'package:flutter/material.dart';
import 'package:safe_road/ui/screens/profile/profile_screen.dart';
import 'package:safe_road/ui/screens/learning/theory_tree_screen.dart';

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

  static const List<Widget> _screens = [
    RoadMapScreen(),
    TheoryTreeScreen(),
    LeaderboardScreen(),
    ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // IndexedStack держит все экраны в памяти (в состоянии сна), сохраняя
      // их scroll-позиции, введенный текст и загруженные из сети данные.
      body: IndexedStack(index: _selectedIndex, children: _screens),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (index) => setState(() => _selectedIndex = index),
        selectedItemColor: AppColors.darkBrownText,
        unselectedItemColor: AppColors.brownText.withValues(alpha: 0.6),
        type: BottomNavigationBarType.fixed,
        items: [
          BottomNavigationBarItem(
            icon: Image.asset("assets/icons/road.png", height: 24),
            label: 'Дорога',
          ),
          BottomNavigationBarItem(
            icon: Image.asset("assets/icons/book.png", height: 24),
            label: 'Теория',
          ),
          BottomNavigationBarItem(
            icon: Image.asset("assets/icons/leader_board.png", height: 24),
            label: 'Рейтинг',
          ),
          BottomNavigationBarItem(
            icon: Image.asset("assets/icons/profile.png", height: 24),
            label: 'Профиль',
          ),
        ],
      ),
    );
  }
}
