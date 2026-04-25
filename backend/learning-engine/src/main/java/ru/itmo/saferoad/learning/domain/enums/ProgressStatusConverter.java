package ru.itmo.saferoad.learning.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ProgressStatusConverter implements AttributeConverter<ProgressStatus, String> {
  @Override
  public String convertToDatabaseColumn(ProgressStatus attribute) {
    return attribute == null ? null : attribute.getDbValue();
  }

  @Override
  public ProgressStatus convertToEntityAttribute(String dbData) {
    return dbData == null ? null : ProgressStatus.fromDbValue(dbData);
  }
}
