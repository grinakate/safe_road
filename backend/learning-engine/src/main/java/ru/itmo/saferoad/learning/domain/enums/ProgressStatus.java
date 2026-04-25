package ru.itmo.saferoad.learning.domain.enums;

import ru.itmo.saferoad.utils.DbEnum;

public enum ProgressStatus implements DbEnum {
  LOCKED,
  UNLOCKED,
  COMPLETED;
  private static final ProgressStatusDescriptor DESCRIPTOR = new ProgressStatusDescriptor();

  @Override
  public String getDbValue() {
    return name();
  }

  public static ProgressStatus fromDbValue(String dbValue) {
    return DESCRIPTOR.fromDatabase(dbValue);
  }
}
