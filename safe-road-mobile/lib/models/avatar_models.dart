class AvatarModel {
  final int id;
  final String url;
  final bool isAvailable;
  final int minLevel;

  AvatarModel({
    required this.id,
    required this.url,
    required this.isAvailable,
    required this.minLevel,
  });

  factory AvatarModel.fromJson(Map<String, dynamic> json) {
    return AvatarModel(
      id: json['id'],
      url: json['url'],
      isAvailable: json['isAvailable'],
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
