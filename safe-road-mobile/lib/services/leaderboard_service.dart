import 'dart:convert';

import 'package:safe_road/models/leaderboard_entry.dart';

import 'api_client.dart';

class LeaderboardService {
  final ApiV1Client _client;

  LeaderboardService(this._client);

  /// Fetch leaderboard. timeframe can be 'week' or 'all'.
  Future<List<LeaderboardEntry>> fetch(String timeframe) async {
    final endpoint = '/gamification/leaderboard?timeframe=$timeframe';
    final resp = await _client.get(endpoint);
    if (resp.statusCode == 200) {
      final decoded = jsonDecode(resp.body);
      final items = _extractItems(decoded);
      return items
          .whereType<Map<String, dynamic>>()
          .map(LeaderboardEntry.fromJson)
          .toList();
    }
    throw Exception('Failed to load leaderboard');
  }

  List<dynamic> _extractItems(dynamic decoded) {
    if (decoded is List) return decoded;
    if (decoded is Map<String, dynamic>) {
      for (final key in const ['items', 'data', 'content', 'leaderboard', 'results']) {
        final value = decoded[key];
        if (value is List) return value;
      }
    }
    throw Exception('Unexpected leaderboard response format');
  }
}

