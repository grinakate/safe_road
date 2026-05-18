package ru.itmo.saferoad.gamification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tools.jackson.databind.JsonNode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementRequirements {

	@NonNull
	private ActionType actionType;

	@NonNull
	private Object value;
}
