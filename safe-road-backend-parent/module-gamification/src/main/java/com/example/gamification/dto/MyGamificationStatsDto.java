package com.example.gamification.dto;

import java.util.List;

public record MyGamificationStatsDto(
		Long userId,
		Long xp,
		List<AchievementDto> achievements
) {
}


