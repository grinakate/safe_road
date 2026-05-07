package ru.itmo.saferoad.core.dto.gamification;

public record AchievementDto(
		Integer id,
		String name,
		String description,
		String iconUrl,
		Integer rewardXp
) {
}

