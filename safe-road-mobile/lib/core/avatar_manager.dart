import 'package:safe_road/models/avatar_models.dart';

class AvatarManager {
  static final Map<int, AvatarModel> _avatarsById = {};
  static final List<AvatarModel> _avatars = [];
  static final int _defaultAvatarId = 1;

  // Метод для инициализации менеджера
  static void initialize(List<AvatarModel> backendAvatarData) {
    _avatarsById.clear();
    _avatarsById.addAll({for (var item in backendAvatarData) item.id: item});
    _avatars.clear();
    _avatars.addAll(backendAvatarData);
    _avatars.sort((a, b) => a.id.compareTo(b.id));
  }

  static AvatarModel getAvatarItem(int avatarId) {
    final item = _avatarsById[avatarId];
    if (item != null) return item;

    final defaultItem = _avatarsById[_defaultAvatarId];
    if (defaultItem != null) return defaultItem;

    if (_avatars.isNotEmpty) return _avatars.first;

    throw StateError('AvatarManager is not initialized or has no avatars');
  }

  static bool get isInitialized => _avatars.isNotEmpty;

  static List<AvatarModel> getAvailableAvatarItems() {
    return _avatars;
  }

  static Map<int, AvatarModel> getAvailableAvatarMap() {
    return _avatarsById;
  }
}
