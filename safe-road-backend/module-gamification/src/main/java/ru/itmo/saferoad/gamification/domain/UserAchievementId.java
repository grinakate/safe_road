package ru.itmo.saferoad.gamification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementId implements Serializable {

	@NonNull
	private Long userId;

	@NonNull
	private Integer achievementId;
}
