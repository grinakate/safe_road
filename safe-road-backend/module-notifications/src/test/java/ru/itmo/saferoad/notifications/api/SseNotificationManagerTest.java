package ru.itmo.saferoad.notifications.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SseNotificationManagerTest {

	@Mock
	private NotificationService notificationService;

	@Mock
	private SseEmitter emitter;

	private SseNotificationManager manager;

	@BeforeEach
	void setUp() {
		manager = new SseNotificationManager(notificationService);
	}

	@Test
	void addAndRemoveEmitter_andGetUserIds() {
		manager.addEmitter(1L, emitter);
		manager.addEmitter(2L, emitter);

		assertTrue(manager.getUserIds().contains(1L));
		assertTrue(manager.getUserIds().contains(2L));

		manager.removeEmitter(1L);
		assertFalse(manager.getUserIds().contains(1L));
		assertTrue(manager.getUserIds().contains(2L));
	}

	@Test
	void sendNotification_success_marksRead() throws Exception {
		Notification n = new Notification();
		n.setId(10L);
		n.setUserId(5L);
		n.setType("T");
		n.setContent("payload");
		n.setCreatedAt(LocalDateTime.now());
		n.setIsRead(false);

		when(notificationService.existingByIdAndLock(10L)).thenReturn(n);
		manager.addEmitter(5L, emitter);

		// emitter.send succeeds (does nothing)
		doNothing().when(emitter).send(ArgumentMatchers.any(SseEmitter.SseEventBuilder.class));

		manager.sendNotification(10L);

		assertTrue(n.getIsRead());
		// emitter should still be present
		assertTrue(manager.getUserIds().contains(5L));
	}

	@Test
	void sendNotification_emitterThrowsIOException_removesEmitter_andDoesNotMarkRead() throws Exception {
		Notification n = new Notification();
		n.setId(11L);
		n.setUserId(6L);
		n.setType("TT");
		n.setContent("p");
		n.setCreatedAt(LocalDateTime.now());
		n.setIsRead(false);

		when(notificationService.existingByIdAndLock(11L)).thenReturn(n);
		manager.addEmitter(6L, emitter);

		doThrow(new IOException("broken socket")).when(emitter).send(ArgumentMatchers.any(SseEmitter.SseEventBuilder.class));

		manager.sendNotification(11L);

		// since send failed, notification should remain unread
		assertFalse(n.getIsRead());
		// emitter should be removed from manager
		assertFalse(manager.getUserIds().contains(6L));
	}

	@Test
	void sendNotification_noEmitter_doesNotMarkRead() {
		Notification n = new Notification();
		n.setId(12L);
		n.setUserId(7L);
		n.setType("X");
		n.setContent("c");
		n.setCreatedAt(LocalDateTime.now());
		n.setIsRead(false);

		when(notificationService.existingByIdAndLock(12L)).thenReturn(n);

		manager.sendNotification(12L);

		assertFalse(n.getIsRead());
		verify(notificationService).existingByIdAndLock(12L);
	}

	@Test
	void sendNotification_propagatesExceptionFromNotificationService() {
		when(notificationService.existingByIdAndLock(77L)).thenThrow(new IllegalArgumentException("missing"));

		assertThrows(IllegalArgumentException.class, () -> manager.sendNotification(77L));
	}

	@Test
	void sendHeartbeat_success_and_ioexception_removal() throws Exception {
		manager.addEmitter(8L, emitter);
		// success path
		doNothing().when(emitter).send(ArgumentMatchers.any(SseEmitter.SseEventBuilder.class));
		manager.sendHeartbeat(8L);
		assertTrue(manager.getUserIds().contains(8L));

		// failing path: add again and throw
		manager.addEmitter(9L, emitter);
		doThrow(new IOException("x")).when(emitter).send(ArgumentMatchers.any(SseEmitter.SseEventBuilder.class));
		manager.sendHeartbeat(9L);
		assertFalse(manager.getUserIds().contains(9L));
	}

	@Test
	void sendHeartbeat_noEmitter_doesNothing() throws Exception {
		manager.sendHeartbeat(123L);

		verifyNoInteractions(emitter);
	}
}



