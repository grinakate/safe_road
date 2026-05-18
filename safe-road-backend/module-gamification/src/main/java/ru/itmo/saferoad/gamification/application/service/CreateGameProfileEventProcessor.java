package ru.itmo.saferoad.gamification.application.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.event.EventProcessor;
import ru.itmo.saferoad.core.event.dto.CreateGameProfileEvent;
import ru.itmo.saferoad.gamification.config.GamificationProperties;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class CreateGameProfileEventProcessor implements EventProcessor {

	private final JsonMapper jsonMapper;
	private final GameProfileService gameProfileService;
	private final GamificationProperties gamificationProperties;

	@Override
	public @NonNull EventType getEventType() {
		return EventType.CREATE_GAME_PROFILE;
	}

	@Override
	public void processEvent(@NonNull PendingEvent event) {
		var userRegisteredEvent = jsonMapper.readValue(event.getContent(), CreateGameProfileEvent.class);
		var userAge = LocalDate.now().until(userRegisteredEvent.getBirthDate(), ChronoUnit.YEARS);
		var isLeaderboardParticipant = userAge >= gamificationProperties.getMinAgeForDefaultLeaderBoarder();
		gameProfileService.create(userRegisteredEvent.getUserId(), isLeaderboardParticipant);
	}
}
