package ru.itmo.saferoad.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LevelEnum {

	START_LEVEL(1);

	private final int number;
}
