package ru.itmo.saferoad.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumDescriptor<E extends Enum<E> & DbEnum> {
  private final Map<String, E> values;

  public EnumDescriptor(Class<E> enumClass) {
    this.values =
        Arrays.stream(enumClass.getEnumConstants())
            .collect(Collectors.toMap(DbEnum::getDbValue, Function.identity()));
  }

  public E fromDatabase(String dbValue) {
    E value = values.get(dbValue);
    if (value == null) {
      throw new IllegalArgumentException("Unknown enum value: " + dbValue);
    }
    return value;
  }
}
