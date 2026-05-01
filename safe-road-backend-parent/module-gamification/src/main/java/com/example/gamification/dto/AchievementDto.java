package com.example.gamification.dto;

public record AchievementDto(
		Integer id,
		String name,
		String description,
		String iconUrl,
		Integer rewardXp
) {
}

