/// Кастомное исключение для ошибок API
class ApiException implements Exception {
  final int statusCode;
  final String message;

  ApiException(this.statusCode, this.message);

  @override
  String toString() => message;
}

/// Кастомное исключение для сетевых проблем (нет интернета, таймаут)
class NetworkException implements Exception {
  final String message;

  NetworkException([this.message = 'Проверьте интернет-соединение']);

  @override
  String toString() => message;
}
