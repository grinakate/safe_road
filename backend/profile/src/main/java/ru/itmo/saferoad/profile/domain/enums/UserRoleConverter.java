package ru.itmo.saferoad.profile.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
  @Override
  public String convertToDatabaseColumn(UserRole attribute) {
    return attribute == null ? null : attribute.getDbValue();
  }

  @Override
  public UserRole convertToEntityAttribute(String dbData) {
    return dbData == null ? null : UserRole.fromDbValue(dbData);
  }
}
