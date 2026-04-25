package ru.itmo.saferoad.profile.domain.enums;

import ru.itmo.saferoad.utils.DbEnum;

public enum UserRole implements DbEnum {
  ADMIN,
  USER,
  MODERATOR;
  private static final UserRoleDescriptor DESCRIPTOR = new UserRoleDescriptor();

  @Override
  public String getDbValue() {
    return name();
  }

  public static UserRole fromDbValue(String dbValue) {
    return DESCRIPTOR.fromDatabase(dbValue);
  }
}
