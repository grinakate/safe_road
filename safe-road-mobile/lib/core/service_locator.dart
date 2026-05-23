import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:safe_road/data/services/learning_service.dart';

import '../data/services/api/api_client_v1.dart';
import '../data/services/auth_service.dart';
import '../data/services/content_service.dart';
import '../data/services/game_profile_service.dart';
import '../data/services/leaderboard_service.dart';
import '../data/services/notification_service.dart';
import '../data/services/storage/token_storage_service.dart';

final getIt = GetIt.instance;

void setupLocator() {
  getIt.registerLazySingleton<TokenStorageService>(() => TokenStorageService());

  // navigatorKey используется для показа диалогов из фонового потока
  getIt.registerLazySingleton<GlobalKey<NavigatorState>>(
    () => GlobalKey<NavigatorState>(),
  );

  getIt.registerLazySingleton<ApiClientV1>(
    () => ApiClientV1(getIt<TokenStorageService>()),
  );

  getIt.registerLazySingleton<AuthService>(
    () => AuthService(getIt<TokenStorageService>(), getIt<ApiClientV1>()),
  );

  getIt.registerLazySingleton<LearningService>(
    () => LearningService(getIt<ApiClientV1>()),
  );

  getIt.registerLazySingleton<ContentService>(
    () => ContentService(getIt<ApiClientV1>()),
  );

  getIt.registerLazySingleton<GameProfileService>(
    () => GameProfileService(getIt<ApiClientV1>()),
  );
  getIt.registerLazySingleton<LeaderboardService>(
    () => LeaderboardService(getIt<ApiClientV1>()),
  );
  getIt.registerLazySingleton<NotificationService>(
    () => NotificationService(getIt<TokenStorageService>()),
  );
}
