class AvatarModel {
  final int id;
  final String name;
  final String url;
  final int minLevel;

  AvatarModel({
    required this.id,
    required this.name,
    required this.url,
    required this.minLevel,
  });

  factory AvatarModel.fromJson(Map<String, dynamic> json) {
    return AvatarModel(
      id: json['id'],
      name: json['name'],
      url: json['url'],
      minLevel: json['minLevel'],
    );
  }
}

class ChangeAvatarRequest {
  final int avatarId;

  ChangeAvatarRequest({required this.avatarId});

  Map<String, dynamic> toJson() => {
    'avatarId': avatarId,
  };
}
