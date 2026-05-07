package ru.itmo.saferoad.core.dto.gamification;

public record UserAchievementsResponse(
		Integer id,
		String title,
		String description,
		String iconUrl,
		boolean isUnlocked,
		int rewardXp
) {
}