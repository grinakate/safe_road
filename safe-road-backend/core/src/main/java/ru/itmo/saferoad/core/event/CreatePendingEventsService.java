package ru.itmo.saferoad.core.event;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;
import ru.itmo.saferoad.core.event.dto.CreateGameProfileEvent;
import ru.itmo.saferoad.core.event.dto.NotificationEvent;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;
import ru.itmo.saferoad.core.event.dto.UnlockFirstTopicEvent;
import ru.itmo.saferoad.core.time.CurrentTime;
import tools.jackson.databind.json.JsonMapper;

@Service
@AllArgsConstructor
public class CreatePendingEventsService {

	private final JsonMapper jsonMapper;
	private final PendingEventsRepository repository;
	private final CurrentTime currentTime;

	public void publishCreateGameProfileEvent(@NonNull CreateGameProfileEvent content) {
		publishEvent(EventType.CREATE_GAME_PROFILE, content);
	}

	public void publishTestSessionCompletedEvent(@NonNull TestSessionCompletedEvent content) {
		publishEvent(EventType.TEST_SESSIONS_COMPLETED, content);
	}

	public void publishUnlockFirstTopicEvent(@NonNull UnlockFirstTopicEvent content) {
		publishEvent(EventType.OPEN_FIRST_TOPIC, content);
	}

	public void publishCreateNotificationEvent(@NonNull NotificationEvent content) {
		publishEvent(EventType.CREATE_NOTIFICATION, content);
	}

	private void publishEvent(@NonNull EventType eventType, Object content) {
		PendingEvent event = new PendingEvent();
		event.setContent(content != null ? jsonMapper.writeValueAsString(content) : null);
		event.setType(eventType);
		event.setStatus(ProgressStatus.PENDING);
		event.setCreatedAt(currentTime.nowDateTime());
		repository.save(event);
	}
}
