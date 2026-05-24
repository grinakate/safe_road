import 'package:dio/dio.dart';

import '../../../core/constants.dart';
import '../../../core/exceptions/api_exceptions.dart';
import '../storage/token_storage_service.dart';

class ApiClientV1 {
  final TokenStorageService _tokenStorageService;
  late final Dio _dio;

  static const String apiVersion = '/api/v1';

  ApiClientV1(this._tokenStorageService) {
    _dio = Dio(
      BaseOptions(
        baseUrl: '${AppConstants.baseUrl}$apiVersion',
        connectTimeout: const Duration(seconds: 15),
        receiveTimeout: const Duration(seconds: 15),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
      ),
    );

    // Добавляем перехватчик для автоматической подстановки токена
    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final requireAuth = options.extra['requireAuth'] ?? true;

          if (requireAuth) {
            final token = await _tokenStorageService.getToken();
            if (token != null) {
              options.headers['Authorization'] = 'Bearer $token';
            }
          }
          return handler.next(options);
        },
      ),
    );
  }

  /// GET запрос
  Future<Response<dynamic>> get(
    String endpoint, {
    bool requireAuth = true,
    Map<String, dynamic>? queryParameters,
  }) async {
    try {
      return await _dio.get(
        endpoint,
        queryParameters: queryParameters,
        options: Options(extra: {'requireAuth': requireAuth}),
      );
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// POST запрос
  Future<Response<dynamic>> post(
    String endpoint,
    dynamic body, {
    bool requireAuth = true,
  }) async {
    try {
      return await _dio.post(
        endpoint,
        data: body,
        options: Options(extra: {'requireAuth': requireAuth}),
      );
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// PATCH запрос
  Future<Response<dynamic>> patch(
    String endpoint,
    dynamic body, {
    bool requireAuth = true,
  }) async {
    try {
      return await _dio.patch(
        endpoint,
        data: body,
        options: Options(extra: {'requireAuth': requireAuth}),
      );
    } on DioException catch (e) {
      throw _handleDioError(e);
    }
  }

  /// Конвертация системных ошибок DioException в ApiException и NetworkException
  Exception _handleDioError(DioException e) {
    if (e.type == DioExceptionType.connectionTimeout ||
        e.type == DioExceptionType.sendTimeout ||
        e.type == DioExceptionType.receiveTimeout ||
        e.type == DioExceptionType.connectionError) {
      return NetworkException(
        'Превышено время ожидания. Проверьте подключение к интернету.',
      );
    }

    if (e.response != null) {
      final statusCode = e.response!.statusCode ?? 500;
      final responseData = e.response!.data;
      String serverMessage = 'Ошибка сервера ($statusCode)';

      if (responseData is Map) {
        serverMessage =
            responseData['message'] ??
            responseData['error'] ??
            responseData['detail'] ??
            serverMessage;
      }

      if (statusCode == 401) {
        return ApiException(401, 'Сессия устарела. Пожалуйста, войдите снова.');
      }

      if (statusCode == 403) {
        return ApiException(403, 'Доступ ограничен.');
      }

      return ApiException(statusCode, serverMessage);
    }

    return NetworkException('Ошибка соединения: ${e.message}');
  }
}
