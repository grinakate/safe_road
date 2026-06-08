package ru.itmo.saferoad.gamification.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementsResponse {
	private int achievementId;
	private String title;
	private String description;
	private String iconUrl;
	private boolean isUnlocked;
	private int rewardXp;
}