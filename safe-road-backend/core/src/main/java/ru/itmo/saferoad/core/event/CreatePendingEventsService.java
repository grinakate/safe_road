package ru.itmo.saferoad.core.event;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class CreatePendingEventsService {

	private final JsonMapper jsonMapper;
	private final PendingEventsRepository repository;

	public void publishUserRegisteredEvent(@NonNull UserRegisteredEvent content) {
		publishEvent(EventType.USER_REGISTERED, content);
	}

	public void publishTestSessionCompletedEvent(@NonNull Long testSessionId) {
		publishEvent(EventType.TEST_SESSIONS_COMPLETED, Map.of("testSessionId", testSessionId));
	}

	private void publishEvent(@NonNull EventType eventType, Object content) {
		PendingEvent event = new PendingEvent();
		event.setContent(content != null ? jsonMapper.writeValueAsString(content) : null);
		event.setType(eventType);
		event.setStatus(ProgressStatus.PENDING);
		event.setCreatedAt(LocalDateTime.now());
		repository.save(event);
	}
}
