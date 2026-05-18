enum TopicStatus {
  LOCKED,
  UNLOCKED,
  COMPLETED;

  static TopicStatus fromString(String status) {
    return TopicStatus.values.firstWhere(
          (e) => e.name == status,
      orElse: () => TopicStatus.LOCKED,
    );
  }
}

