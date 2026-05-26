package ru.itmo.saferoad.learning.application.scheduler;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;
import ru.itmo.saferoad.core.event.EventExecutor;

import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningPendingEventsSchedulerTest {

	@Mock
	private PendingEventsRepository pendingEventsRepository;

	@Mock
	private EventExecutor eventExecutor;

	@Mock
	private LearningPendingEventsProperties properties;

	private LearningPendingEventsScheduler scheduler;

	@Test
	void processPendingEvents_noEvents_shouldNotCallExecutor() {
		when(properties.getScheduledTreadCount()).thenReturn(10);
		when(pendingEventsRepository.findByStatusAndTypeIn(any(), any(), any())).thenReturn(List.of());

		scheduler = new LearningPendingEventsScheduler(pendingEventsRepository, eventExecutor, properties);

		scheduler.processPendingEvents();

		verify(eventExecutor, never()).process(any());
	}

	@Test
	void processPendingEvents_withEvents_shouldSubmitToExecutor() {
		when(properties.getScheduledTreadCount()).thenReturn(1);
		PendingEvent e = Instancio.of(PendingEvent.class)
				.set(field(PendingEvent::getId), 1L)
				.set(field(PendingEvent::getContent), "{}")
				.create();
		when(pendingEventsRepository.findByStatusAndTypeIn(any(), any(), any())).thenReturn(List.of(e));

		scheduler = new LearningPendingEventsScheduler(pendingEventsRepository, eventExecutor, properties);

		scheduler.processPendingEvents();

		verify(eventExecutor, timeout(1000).times(1)).process(e);
	}
}

