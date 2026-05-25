import 'dart:async';

import 'package:flutter_client_sse/constants/sse_request_type_enum.dart'
    as sse_consts;
import 'package:flutter_client_sse/flutter_client_sse.dart' as sse;

import '../../core/constants.dart';
import 'api/api_client_v1.dart';
import 'storage/token_storage_service.dart';

class NotificationEvent {
  final String title;
  final String content;

  NotificationEvent({required this.title, required this.content});
}

class NotificationService {
  final TokenStorageService _tokenStorage;
  final _eventController = StreamController<NotificationEvent>.broadcast();

  Stream<NotificationEvent> get events => _eventController.stream;

  StreamSubscription<sse.SSEModel>? _subscription;
  String? _lastEventId;

  NotificationService(this._tokenStorage);

  void start() => _startListening();

  void _startListening() async {
    _cancelSubscription();
    final uri =
        '${AppConstants.baseUrl}${ApiClientV1.apiVersion}/notifications/subscribe';

    final token = await _tokenStorage.getToken();
    final headers = {
      'Accept': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive',
      if (token != null) 'Authorization': 'Bearer $token',
      if (_lastEventId != null) 'Last-Event-ID': _lastEventId!,
    };

    _subscription =
        sse.SSEClient.subscribeToSSE(
          method: sse_consts.SSERequestType.GET,
          url: uri,
          header: headers,
        ).listen((model) {
          if (model.id != null) _lastEventId = model.id;
          final evt = NotificationEvent(
            title: (model.event ?? 'notification').trim(),
            content: (model.data ?? '').trim(),
          );
          if (evt.content.isNotEmpty) _eventController.add(evt);
        });
  }

  void _cancelSubscription() {
    _subscription?.cancel();
    sse.SSEClient.unsubscribeFromSSE();
    _subscription = null;
  }

  void dispose() {
    _cancelSubscription();
    _eventController.close();
  }
}
