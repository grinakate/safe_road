import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:safe_road/models/leaderboard_entry.dart';
import 'package:safe_road/services/leaderboard_service.dart';

class LeaderboardProvider extends ChangeNotifier {
  final LeaderboardService _service = GetIt.instance<LeaderboardService>();

  List<LeaderboardEntry> _week = [];
  List<LeaderboardEntry> _all = [];

  bool _loadingWeek = false;
  bool _loadingAll = false;

  List<LeaderboardEntry> get week => _week;
  List<LeaderboardEntry> get all => _all;
  bool get loadingWeek => _loadingWeek;
  bool get loadingAll => _loadingAll;

  Future<void> fetchWeek() async {
    _loadingWeek = true;
    notifyListeners();
    try {
      _week = await _service.fetch('week');
    } catch (_) {
      _week = [];
    }
    _loadingWeek = false;
    notifyListeners();
  }

  Future<void> fetchAll() async {
    _loadingAll = true;
    notifyListeners();
    try {
      _all = await _service.fetch('all');
    } catch (_) {
      _all = [];
    }
    _loadingAll = false;
    notifyListeners();
  }
}

