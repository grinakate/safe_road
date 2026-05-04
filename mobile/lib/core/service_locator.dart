import 'package:get_it/get_it.dart';

import '../services/api_client.dart';
import '../services/auth_service.dart';
import '../services/map_service.dart';
import '../services/token_storage_service.dart';
import '../services/user_service.dart';

final getIt = GetIt.instance;

void setupLocator() {
  getIt.registerLazySingleton<TokenStorageService>(() => TokenStorageService());

  getIt.registerLazySingleton<ApiClient>(
    () => ApiClient(getIt<TokenStorageService>()),
  );

  getIt.registerLazySingleton<AuthService>(
    () => AuthService(getIt<TokenStorageService>(), ApiClient(getIt<TokenStorageService>())),
  );

  getIt.registerLazySingleton<MapService>(() => MapService(getIt<ApiClient>()));

  getIt.registerLazySingleton(() => UserService(getIt<ApiClient>()));
}
