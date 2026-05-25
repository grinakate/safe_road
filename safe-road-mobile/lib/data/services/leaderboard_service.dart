import 'package:safe_road/data/models/gamification/leaderboard_entry.dart';

import '../../logic/providers/leaderboard_state.dart';
import 'api/api_client_v1.dart';

class LeaderboardService {
  final ApiClientV1 _client;

  LeaderboardService(this._client);

  Future<List<LeaderboardEntry>> fetch(LeaderboardPeriod period) async {
    final resp = await _client.get(
      '/gamification/leaderboard',
      queryParameters: {'timeframe': period.apiKey},
    );

    final items = _extractItems(resp.data);

    return items
        .whereType<Map<String, dynamic>>()
        .map(LeaderboardEntry.fromJson)
        .toList();
  }

  /// Помогаем извлечь список элементов, если бэкенд обернул его в пагинацию
  List<dynamic> _extractItems(dynamic data) {
    if (data is List) return data;
    if (data is Map<String, dynamic>) {
      for (final key in const [
        'items',
        'data',
        'content',
        'leaderboard',
        'results',
      ]) {
        final value = data[key];
        if (value is List) return value;
      }
    }
    throw Exception('Неожиданный формат ответа таблицы лидеров');
  }
}
