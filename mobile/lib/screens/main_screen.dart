import 'package:flutter/material.dart';
import 'package:safe_road/screens/profile_screen.dart';

import 'map_screen.dart';

class MainScreen extends StatefulWidget {
  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _selectedIndex = 0;

  // Список экранов для переключения
  final List<Widget> _screens = [
    MapScreen(),
    const Center(child: Text("Лидерборды")),
    ProfileScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_selectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: (index) => setState(() => _selectedIndex = index),
        items: [
          BottomNavigationBarItem(
            icon: Image.asset("assets/icons/road.png", height: 24,),
            label: 'Дорога',
          ),
          BottomNavigationBarItem(
            icon: Image.asset("assets/icons/leader_board.png", height: 24,),
            label: 'Рейтинг',
          ),
          BottomNavigationBarItem(
            icon: Image.asset("assets/icons/profile.png", height: 24,),
            label: 'Профиль',
          ),
        ],
      ),
    );
  }
}
