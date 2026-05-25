package ru.itmo.saferoad.gamification.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGameProfileDto {

	@NotNull
	private String name;

	@NotNull
	private Integer level;

	@NotNull
	private Integer currentXp;

	@NotNull
	private Double xpProgress;

	@NotNull
	private UserAvatarDto avatar;

	@NotNull
	private Integer currentStreak;

	@NotNull
	private Boolean leaderboardEnabled;

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserAvatarDto{
		private Integer id;
		private String url;
	}
}


