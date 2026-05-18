import 'package:get_it/get_it.dart';
import 'package:safe_road/services/quiz_service.dart';

import '../services/api_client.dart';
import '../services/auth_service.dart';
import '../services/road_map_service.dart';
import '../services/token_storage_service.dart';
import '../services/user_service.dart';

final getIt = GetIt.instance;

void setupLocator() {
  getIt.registerLazySingleton<TokenStorageService>(() => TokenStorageService());

  getIt.registerLazySingleton<ApiV1Client>(
    () => ApiV1Client(getIt<TokenStorageService>()),
  );

  getIt.registerLazySingleton<AuthService>(
    () => AuthService(getIt<TokenStorageService>(), ApiV1Client(getIt<TokenStorageService>())),
  );

  getIt.registerLazySingleton<LearningService>(() => LearningService(getIt<ApiV1Client>()));

  getIt.registerLazySingleton(() => UserService(getIt<ApiV1Client>()));
  getIt.registerLazySingleton(() => QuizService(getIt<ApiV1Client>()));
}
