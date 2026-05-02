package ru.itmo.saferoad.gamification.dto;

public record UserAchievementsResponse(
		Integer id,
		String title,
		String description,
		String iconUrl,
		boolean isUnlocked
) {
}