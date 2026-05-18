package ru.itmo.saferoad.learning.application.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.event.EventProcessor;
import ru.itmo.saferoad.core.event.dto.UnlockFirstTopicEvent;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;
import tools.jackson.databind.json.JsonMapper;

@Service
@AllArgsConstructor
public class UnlockFirstTopicEventProcessor implements EventProcessor {

	private final JsonMapper jsonMapper;
	private final UserTopicProgressService userTopicProgressService;
	private final SectionService sectionService;

	@Override
	public @NonNull EventType getEventType() {
		return EventType.OPEN_FIRST_TOPIC;
	}

	@Override
	public void processEvent(@NonNull PendingEvent event) {
		Section firstSection = sectionService.getFirstSection();
		Topic firstTopic = firstSection.getTopics().getFirst();
		var openFirstTopicEvent = jsonMapper.readValue(event.getContent(), UnlockFirstTopicEvent.class);
		userTopicProgressService.unlockTopic(openFirstTopicEvent.getUserId(), firstTopic);
	}
}
