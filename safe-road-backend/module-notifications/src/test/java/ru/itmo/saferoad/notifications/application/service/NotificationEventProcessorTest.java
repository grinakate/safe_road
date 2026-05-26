package ru.itmo.saferoad.notifications.application.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.event.dto.NotificationEvent;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;
import ru.itmo.saferoad.notifications.service.NotificationService;
import tools.jackson.databind.json.JsonMapper;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationEventProcessorTest {

	@Mock
	private JsonMapper jsonMapper;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private NotificationEventProcessor processor;

	@Test
	void getEventType_shouldReturnCreateNotification() {
		assertEquals(ru.itmo.saferoad.core.domain.enums.EventType.CREATE_NOTIFICATION, processor.getEventType());
	}

	@Test
	void processEvent_validPayload_callsCreate() throws Exception {
		PendingEvent e = Instancio.of(PendingEvent.class)
				.set(field(PendingEvent::getId), 1L)
				.set(field(PendingEvent::getContent), "{}")
				.create();

		NotificationEvent payload = new NotificationEvent(11L, NotificationType.SYSTEM_MESSAGE, "{\"x\":1}");
		when(jsonMapper.readValue(e.getContent(), NotificationEvent.class)).thenReturn(payload);

		processor.processEvent(e);

		verify(notificationService).create(11L, NotificationType.SYSTEM_MESSAGE, "{\"x\":1}");
	}

	@Test
	void processEvent_whenJsonParsingFails_throwsRuntime() throws Exception {
		PendingEvent e = Instancio.of(PendingEvent.class)
				.set(field(PendingEvent::getId), 2L)
				.set(field(PendingEvent::getContent), "bad")
				.create();

		when(jsonMapper.readValue(e.getContent(), NotificationEvent.class)).thenThrow(new RuntimeException("oops"));

		assertThrows(RuntimeException.class, () -> processor.processEvent(e));
	}
}

