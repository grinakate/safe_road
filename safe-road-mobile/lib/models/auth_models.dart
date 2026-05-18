import 'dart:convert';

class LoginRequest {
  final String email;
  final String password;

  LoginRequest({required this.email, required this.password});

  Map<String, dynamic> toJson() => {'email': email, 'password': password};
}

class TokenResponse {
  final String token;

  TokenResponse({required this.token});

  factory TokenResponse.fromJson(Map<String, dynamic> json) {
    return TokenResponse(token: json['token']);
  }
}

class UserRegisterRequest {
  final String nickname;
  final String email;
  final DateTime birthDate;
  final String password;

  UserRegisterRequest({
    required this.nickname,
    required this.email,
    required this.birthDate,
    required this.password,
  });

  Map<String, dynamic> toJson() => {
    'nickname': nickname,
    'email': email,
    'birthDate':
        "${birthDate.year.toString().padLeft(4, '0')}-${birthDate.month.toString().padLeft(2, '0')}-${birthDate.day.toString().padLeft(2, '0')}",
    'password': password,
  };
}
