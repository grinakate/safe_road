package ru.itmo.saferoad.gamification.controller.dto;

public record UserAchievementsResponse(
		String title,
		String description,
		String iconUrl,
		boolean isUnlocked,
		int rewardXp
) {
}