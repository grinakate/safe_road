package ru.itmo.saferoad.gamification.dto;

public record AchievementDto(
		Integer id,
		String name,
		String description,
		String iconUrl,
		Integer rewardXp
) {
}

