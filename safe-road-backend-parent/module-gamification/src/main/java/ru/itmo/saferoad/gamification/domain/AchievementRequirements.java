package ru.itmo.saferoad.gamification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementRequirements {

	@NonNull
	private ActionType actionType;

	@NonNull
	private Integer value;
}
