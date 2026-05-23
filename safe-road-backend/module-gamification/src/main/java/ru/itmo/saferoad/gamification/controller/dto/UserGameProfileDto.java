package ru.itmo.saferoad.gamification.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGameProfileDto {
	private String name;
	private Integer level;
	private Integer currentXp;
	private Double xpProgress;
	private UserAvatarDto avatar;
	private Integer currentStreak;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserAvatarDto{
		private Integer id;
		private String url;
	}
}


