import 'package:flutter/cupertino.dart';
import 'package:get_it/get_it.dart';

import '../../data/services/leaderboard_service.dart';
import 'leaderboard_state.dart';

class LeaderboardProvider extends ChangeNotifier {
  final LeaderboardService _service = GetIt.instance<LeaderboardService>();

  // Карта состояний для каждого периода
  final Map<LeaderboardPeriod, LeaderboardState> _states = {
    for (var period in LeaderboardPeriod.values) period: LeaderboardState(),
  };

  // Геттер для получения состояния конкретного периода
  LeaderboardState getState(LeaderboardPeriod period) => _states[period]!;

  /// Единый метод загрузки для любого периода
  Future<void> fetch(LeaderboardPeriod period) async {
    final currentState = _states[period]!;

    // Определяем: это первая загрузка или обновление?
    final bool isInitial = currentState.entries.isEmpty;

    // Обновляем статус загрузки
    _states[period] = currentState.copyWith(
      status: isInitial ? LoadingStatus.loading : LoadingStatus.refreshing,
      errorMessage: null,
    );
    notifyListeners();

    try {
      final data = await _service.fetch(period);

      _states[period] = _states[period]!.copyWith(
        entries: data,
        status: LoadingStatus.idle,
      );
    } catch (e) {
      _states[period] = _states[period]!.copyWith(
        status: LoadingStatus.error,
        errorMessage: e.toString(),
      );
    } finally {
      notifyListeners();
    }
  }
}
