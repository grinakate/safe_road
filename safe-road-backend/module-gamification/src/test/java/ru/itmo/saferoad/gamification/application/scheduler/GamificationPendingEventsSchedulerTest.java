package ru.itmo.saferoad.gamification.application.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;
import ru.itmo.saferoad.core.event.EventExecutor;

import java.util.List;
import org.instancio.Instancio;
import static org.instancio.Select.field;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamificationPendingEventsSchedulerTest {

	@Mock
	private PendingEventsRepository pendingEventsRepository;

	@Mock
	private EventExecutor eventExecutor;

	@Mock
	private GamificationPendingEventsProperties properties;

	private GamificationPendingEventsScheduler scheduler;

	@Test
	void processPendingEvents_noEvents_shouldNotCallExecutor() {
		// Given
		when(properties.getScheduledTreadCount()).thenReturn(10);
		when(pendingEventsRepository.findByStatusAndTypeIn(any(), any(), any())).thenReturn(List.of());

		// instantiate scheduler after stubbing properties so constructor uses valid pool size
		scheduler = new GamificationPendingEventsScheduler(pendingEventsRepository, eventExecutor, properties);

		// When
		scheduler.processPendingEvents();

		// Then
		verify(eventExecutor, never()).process(any());
	}

	@Test
	void processPendingEvents_withEvents_shouldSubmitToExecutor() {
		// Given
		when(properties.getScheduledTreadCount()).thenReturn(1);
		PendingEvent e = Instancio.of(PendingEvent.class)
				.set(field(PendingEvent::getId), 1L)
				.set(field(PendingEvent::getContent), "{}")
				.create();
		when(pendingEventsRepository.findByStatusAndTypeIn(any(), any(), any())).thenReturn(List.of(e));

		// instantiate scheduler after stubbing properties so constructor uses valid pool size
		scheduler = new GamificationPendingEventsScheduler(pendingEventsRepository, eventExecutor, properties);

		// When
		scheduler.processPendingEvents();

		// Then
		verify(eventExecutor, timeout(1000).times(1)).process(e);
	}
}




