package ru.itmo.saferoad.gamification.controller.dto;

public record LeaderboardEntryDto(
		Long rank,
		String avatarUrl,
		String nickname,
		Integer xp,
		Boolean isCurrentUser
) {
}


