import '../../data/models/gamification/leaderboard_entry.dart';

enum LeaderboardPeriod {
  week('Неделя'),
  //month('За месяц'),
  all('Все время');

  final String label;

  const LeaderboardPeriod(this.label);

  String get apiKey => name;
}

enum LoadingStatus { idle, loading, refreshing, error }

class LeaderboardState {
  final List<LeaderboardEntry> entries;
  final LoadingStatus status;
  final String? errorMessage;

  LeaderboardState({
    this.entries = const [],
    this.status = LoadingStatus.idle,
    this.errorMessage,
  });

  LeaderboardState copyWith({
    List<LeaderboardEntry>? entries,
    LoadingStatus? status,
    String? errorMessage,
  }) {
    return LeaderboardState(
      entries: entries ?? this.entries,
      status: status ?? this.status,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }
}
