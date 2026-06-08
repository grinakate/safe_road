package ru.itmo.saferoad.core.event.dto.notifications;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementEarnedEventData {

	@NotNull
	private Integer achievementId;

	@NotNull
	private String title;

	@NotNull
	private String iconUrl;

	@NotNull
	private Integer earnedXp;
}

