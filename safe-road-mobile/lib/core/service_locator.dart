import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:safe_road/services/learning_service.dart';

import '../services/api_client.dart';
import '../services/auth_service.dart';
import '../services/game_profile_service.dart';
import '../services/notification_service.dart';
import '../services/token_storage_service.dart';

final getIt = GetIt.instance;

void setupLocator() {
  getIt.registerLazySingleton<TokenStorageService>(() => TokenStorageService());

  // navigatorKey используется для показа диалогов из фонового потока
  getIt.registerLazySingleton<GlobalKey<NavigatorState>>(
    () => GlobalKey<NavigatorState>(),
  );

  getIt.registerLazySingleton<ApiV1Client>(
    () => ApiV1Client(getIt<TokenStorageService>()),
  );

  getIt.registerLazySingleton<AuthService>(
    () => AuthService(getIt<TokenStorageService>(), getIt<ApiV1Client>()),
  );

  getIt.registerLazySingleton<LearningService>(
    () => LearningService(getIt<ApiV1Client>()),
  );

  getIt.registerLazySingleton<GameProfileService>(
    () => GameProfileService(getIt<ApiV1Client>()),
  );
  getIt.registerLazySingleton<NotificationService>(
    () => NotificationService(
      getIt<ApiV1Client>(),
      getIt<GlobalKey<NavigatorState>>(),
    ),
  );
}
