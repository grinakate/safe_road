import 'package:safe_road/models/level_model.dart';

class UserModel {
  final int id;
  final String username;
  final String email;
  final DateTime birthDate;
  final bool emailVerified;
  final int currentXp;
  final int coins;
  final LevelModel level;
  final String avatarUrl;

  UserModel({
    required this.id,
    required this.username,
    required this.email,
    required this.emailVerified,
    required this.birthDate,
    required this.currentXp,
    required this.coins,
    required this.avatarUrl,
    required this.level
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'],
      username: json['name'],
      email: json['email'],
      emailVerified: json['emailVerified'],
      birthDate: DateTime.parse(json['birthDate']),
      currentXp: json['currentXp'],
      coins: json['coins'],
      avatarUrl: json['avatarUrl'],
      level: LevelModel.fromJson(json['level'])
    );
  }
}
