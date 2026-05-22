package ru.itmo.saferoad.gamification.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyGamificationStatsDto {
	private String name;
	private Integer level;
	private Integer currentXp;
	private Double xpProgress;
	private Integer avatarId;
	private Integer currentStreak;
}


