import 'package:flutter/material.dart';
import 'package:safe_road/data/models/learning/topic_content.dart';
import 'package:safe_road/data/services/content_service.dart';

import '../../core/service_locator.dart';

class TopicProvider extends ChangeNotifier {
  final ContentService _service;

  final Map<int, TopicContent> _cache = {};
  final Map<int, Future<TopicContent>> _inflight = {};

  String? _lastError;

  String? get lastError => _lastError;

  TopicContent? getCached(int id) => _cache[id];

  bool hasCached(int id) => _cache.containsKey(id);

  Future<TopicContent> getTopic(int id) async {
    if (_cache.containsKey(id)) return _cache[id]!;
    if (_inflight.containsKey(id)) return _inflight[id]!;

    _lastError = null;
    final Future<TopicContent> future = _service.fetchTopic(id);
    _inflight[id] = future;

    try {
      final topic = await future;
      _cache[id] = topic;

      // 2. Безопасный вызов уведомления
      _safeNotify();
      return topic;
    } catch (e) {
      _lastError = e.toString();
      _safeNotify();
      rethrow;
    } finally {
      _inflight.remove(id);
    }
  }

  Future<TopicContent> refreshTopic(int id) async {
    _cache.remove(id);
    return getTopic(id);
  }

  void clear() {
    _cache.clear();
    _inflight.clear();
    _safeNotify();
  }

  bool _isDisposed = false;

  TopicProvider(this._service);

  @override
  void dispose() {
    _isDisposed = true;
    super.dispose();
  }

  void _safeNotify() {
    if (!_isDisposed) notifyListeners();
  }
}
