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
  bool _refreshingWeek = false;
  bool _refreshingAll = false;

  List<LeaderboardEntry> get week => _week;
  List<LeaderboardEntry> get all => _all;
  bool get loadingWeek => _loadingWeek;
  bool get loadingAll => _loadingAll;

  Future<void> fetchWeek() async {
    // If we already have cached data, perform a background refresh so UI can show cached
    // immediately and then update when network call completes.
    if (_week.isNotEmpty) {
      _refreshingWeek = true;
      notifyListeners();
      try {
        final fresh = await _service.fetch('week');
        _week = fresh;
      } catch (_) {
        // keep existing cache on error
      }
      _refreshingWeek = false;
      notifyListeners();
      return;
    }

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
    if (_all.isNotEmpty) {
      _refreshingAll = true;
      notifyListeners();
      try {
        final fresh = await _service.fetch('all');
        _all = fresh;
      } catch (_) {}
      _refreshingAll = false;
      notifyListeners();
      return;
    }

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

  bool get refreshingWeek => _refreshingWeek;
  bool get refreshingAll => _refreshingAll;
}

