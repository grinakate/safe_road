class Avatar {
  final int id;
  final String url;
  final bool isAvailable;
  final int minLevel;

  Avatar({
    required this.id,
    required this.url,
    required this.isAvailable,
    required this.minLevel,
  });

  factory Avatar.fromJson(Map<String, dynamic> json) {
    return Avatar(
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
