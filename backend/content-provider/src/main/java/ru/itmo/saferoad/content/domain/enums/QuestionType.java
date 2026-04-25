package ru.itmo.saferoad.content.domain.enums;

import ru.itmo.saferoad.utils.DbEnum;

public enum QuestionType implements DbEnum {
  CHOICE,
  MATCH;
  private static final QuestionTypeDescriptor DESCRIPTOR = new QuestionTypeDescriptor();

  @Override
  public String getDbValue() {
    return name();
  }

  public static QuestionType fromDbValue(String dbValue) {
    return DESCRIPTOR.fromDatabase(dbValue);
  }
}
