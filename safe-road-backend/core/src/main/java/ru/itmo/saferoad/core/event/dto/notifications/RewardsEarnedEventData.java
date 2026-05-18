package ru.itmo.saferoad.core.event.dto.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardsEarnedEventData {

	@NonNull
	private Integer earnedXp;

	@NonNull
	private Integer totalXp;

	@NonNull
	private Boolean levelUp;

	private Integer newLevel;
}
