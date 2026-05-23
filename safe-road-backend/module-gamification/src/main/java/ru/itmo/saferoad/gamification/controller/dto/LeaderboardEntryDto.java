package ru.itmo.saferoad.gamification.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDto {
	private Long rank;
	private String avatarUrl;
	private String nickname;
	private Long xp;
	private Boolean isCurrentUser;
}


