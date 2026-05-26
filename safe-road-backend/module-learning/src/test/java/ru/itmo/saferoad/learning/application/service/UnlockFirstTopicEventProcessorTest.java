package ru.itmo.saferoad.learning.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.event.dto.UnlockFirstTopicEvent;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnlockFirstTopicEventProcessorTest {

	@Mock
	private JsonMapper jsonMapper;

	@Mock
	private UserTopicProgressService userTopicProgressService;

	@Mock
	private SectionService sectionService;

	@InjectMocks
	private UnlockFirstTopicEventProcessor processor;

	@Test
	void getEventType_returnsOpenFirstTopic() {
		assertEquals(ru.itmo.saferoad.core.domain.enums.EventType.OPEN_FIRST_TOPIC, processor.getEventType());
	}

	@Test
	void processEvent_whenNoTopics_doesNotCallUnlock() {
		Section s = mock(Section.class);
		when(s.getTopics()).thenReturn(List.of());
		when(sectionService.getFirstSection()).thenReturn(s);

		PendingEvent e = new PendingEvent();
		e.setContent("{}");
		UnlockFirstTopicEvent payload = new UnlockFirstTopicEvent(11L);
		when(jsonMapper.readValue(e.getContent(), UnlockFirstTopicEvent.class)).thenReturn(payload);

		processor.processEvent(e);

		verify(userTopicProgressService, times(0)).unlockTopic(eq(11L), org.mockito.Mockito.any());
	}

	@Test
	void processEvent_whenTopicExists_callsUnlockTopic() {
		Topic t = new Topic();
		t.setId(100);
		Section s = mock(Section.class);
		when(s.getTopics()).thenReturn(List.of(t));
		when(sectionService.getFirstSection()).thenReturn(s);

		PendingEvent e = new PendingEvent();
		e.setContent("{}");
		UnlockFirstTopicEvent payload = new UnlockFirstTopicEvent(22L);
		when(jsonMapper.readValue(e.getContent(), UnlockFirstTopicEvent.class)).thenReturn(payload);

		processor.processEvent(e);

		verify(userTopicProgressService, times(1)).unlockTopic(22L, t);
	}
}

