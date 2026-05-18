package ru.itmo.saferoad.gamification.controller.dto;

public record MyGamificationStatsDto(
		String name,
		Integer level,
		Integer currentXp,
		Double xpProgress,
		Integer avatarId,
		Integer currentStreak
) {
}


