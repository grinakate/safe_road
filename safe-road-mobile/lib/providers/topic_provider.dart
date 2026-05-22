import 'package:flutter/material.dart';
import 'package:safe_road/models/topic_content.dart';
import 'package:safe_road/services/content_service.dart';
import '../core/service_locator.dart';

/// Provider that caches TopicContent fetched from server to avoid duplicate calls.
class TopicProvider extends ChangeNotifier {
  final ContentService _service = getIt<ContentService>();

  final Map<int, TopicContent> _cache = {};
  final Map<int, Future<TopicContent>> _inflight = {};

  TopicContent? getCached(int id) => _cache[id];

  bool hasCached(int id) => _cache.containsKey(id);

  /// Get topic content, using cache when available. Concurrent requests for same id share the same future.
  Future<TopicContent> getTopic(int id) async {
    if (_cache.containsKey(id)) return _cache[id]!;
    if (_inflight.containsKey(id)) return _inflight[id]!;

    final future = _service.fetchTopic(id).then((topic) {
      _cache[id] = topic;
      _inflight.remove(id);
      notifyListeners();
      return topic;
    }).catchError((e) {
      _inflight.remove(id);
      throw e;
    });

    _inflight[id] = future;
    return future;
  }

  /// Optional: force refresh from network
  Future<TopicContent> refreshTopic(int id) async {
    _cache.remove(id);
    return getTopic(id);
  }

  void clearCache() {
    _cache.clear();
    notifyListeners();
  }
}

