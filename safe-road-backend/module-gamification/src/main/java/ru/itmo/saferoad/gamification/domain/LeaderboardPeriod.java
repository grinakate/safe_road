package ru.itmo.saferoad.gamification.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaderboardPeriod {

	WEEK("week"),
	ALL("all");

	private final String value;
}
