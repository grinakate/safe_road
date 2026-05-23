import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:safe_road/data/services/learning_service.dart';
import 'package:safe_road/logic/providers/auth_provider.dart';
import 'package:safe_road/logic/providers/notification_provider.dart';

import '../data/services/api/api_client_v1.dart';
import '../data/services/auth_service.dart';
import '../data/services/content_service.dart';
import '../data/services/game_profile_service.dart';
import '../data/services/leaderboard_service.dart';
import '../data/services/notification_service.dart';
import '../data/services/storage/token_storage_service.dart';
import '../logic/providers/game_profile_provider.dart';

final getIt = GetIt.instance;

void setupLocator() {
  getIt.registerLazySingleton<TokenStorageService>(() => TokenStorageService());

  getIt.registerLazySingleton<GlobalKey<NavigatorState>>(
    () => GlobalKey<NavigatorState>(),
  );

  getIt.registerLazySingleton<ApiClientV1>(
    () => ApiClientV1(getIt<TokenStorageService>()),
  );

  getIt.registerLazySingleton<AuthService>(
    () => AuthService(getIt<TokenStorageService>(), getIt<ApiClientV1>()),
  );
  getIt.registerLazySingleton<AuthProvider>(
        () => AuthProvider(getIt<AuthService>()),
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
  getIt.registerLazySingleton<GameProfileProvider>(
    () => GameProfileProvider(getIt<GameProfileService>()),
  );

  getIt.registerLazySingleton<LeaderboardService>(
    () => LeaderboardService(getIt<ApiClientV1>()),
  );

  getIt.registerLazySingleton<NotificationService>(
    () => NotificationService(getIt<TokenStorageService>()),
  );
  getIt.registerLazySingleton<NotificationProvider>(
    () => NotificationProvider(
      getIt<NotificationService>(),
      getIt<GameProfileProvider>(),
      getIt<GlobalKey<NavigatorState>>(),
    ),
  );
}
