package ru.itmo.saferoad.core.dto.gamification;

import java.util.List;

public record MyGamificationStatsDto(
		Long userId,
		Long xp,
		List<AchievementDto> achievements
) {
}


