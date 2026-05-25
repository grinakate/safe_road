package ru.itmo.saferoad.notifications.api;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.notifications.config.NotificationsProperties;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.dto.NotificationDto;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

	@Mock
	private SseNotificationManager sseNotificationManager;

	@Mock
	private NotificationService notificationService;

	@Mock
	private NotificationsProperties notificationsProperties;

	@Mock
	private java.util.concurrent.ExecutorService executorService;

	private NotificationController controller;

	@Test
	void subscribe_addsEmitterForUser() {
		// Given
		AppUserDetails user = mock(AppUserDetails.class);
		when(user.getId()).thenReturn(42L);

		when(notificationsProperties.getSseTimeoutMs()).thenReturn(5_000L);

		controller = new NotificationController(executorService, sseNotificationManager, notificationService, notificationsProperties);

		// When
		var emitter = controller.subscribe(user);

		// Then
		assertNotNull(emitter);
		verify(sseNotificationManager, times(1)).addEmitter(eq(42L), any());
	}

	@Test
	void getNotifications_mapsToDto() {
		// Given
		AppUserDetails user = mock(AppUserDetails.class);
		when(user.getId()).thenReturn(2L);

		Notification n = Instancio.of(Notification.class)
				.set(field(Notification::getId), 11L)
				.set(field(Notification::getUserId), 2L)
				.set(field(Notification::getType), "T")
				.set(field(Notification::getContent), "{\"x\":1}")
				.set(field(Notification::getCreatedAt), LocalDateTime.now())
				.set(field(Notification::getIsRead), false)
				.create();

		when(notificationService.getByUserId(2L)).thenReturn(List.of(n));

		controller = new NotificationController(executorService, sseNotificationManager, notificationService, notificationsProperties);

		// When
		ResponseEntity<List<NotificationDto>> resp = controller.getNotifications(user);

		// Then
		assertNotNull(resp.getBody());
		assertEquals(1, resp.getBody().size());
		NotificationDto dto = resp.getBody().get(0);
		assertEquals(11L, dto.getId());
		assertEquals("T", dto.getTitle());
	}

	@Test
	void getUnreadNotifications_mapsToDto() {
		// Given
		AppUserDetails user = mock(AppUserDetails.class);
		when(user.getId()).thenReturn(3L);

		Notification n = Instancio.of(Notification.class)
				.set(field(Notification::getId), 21L)
				.set(field(Notification::getUserId), 3L)
				.set(field(Notification::getType), "U")
				.set(field(Notification::getContent), "{}")
				.set(field(Notification::getCreatedAt), LocalDateTime.now())
				.set(field(Notification::getIsRead), false)
				.create();

		when(notificationService.getUnreadByUserId(3L)).thenReturn(List.of(n));

		controller = new NotificationController(executorService, sseNotificationManager, notificationService, notificationsProperties);

		// When
		ResponseEntity<List<NotificationDto>> resp = controller.getUnreadNotifications(user);

		// Then
		assertEquals(1, resp.getBody().size());
		assertEquals(21L, resp.getBody().get(0).getId());
	}

	@Test
	void getUnreadCount_delegatesToService() {
		// Given
		AppUserDetails user = mock(AppUserDetails.class);
		when(user.getId()).thenReturn(7L);
		when(notificationService.getUnreadCount(7L)).thenReturn(5L);

		controller = new NotificationController(executorService, sseNotificationManager, notificationService, notificationsProperties);

		// When
		ResponseEntity<Long> resp = controller.getUnreadCount(user);

		// Then
		assertEquals(5L, resp.getBody());


	}

	@Test
	void markAsRead_callsService_andReturnsNoContent() {
		// Given
		AppUserDetails user = mock(AppUserDetails.class);


		controller = new NotificationController(executorService, sseNotificationManager, notificationService, notificationsProperties);

		// When
		var resp = controller.markAsRead(100L, user);

		// Then
		verify(notificationService, times(1)).markAsRead(100L);
		assertEquals(204, resp.getStatusCode().value());
	}

	@Test
	void markAllAsRead_callsService_andReturnsNoContent() {
		// Given
		AppUserDetails user = mock(AppUserDetails.class);
		when(user.getId()).thenReturn(8L);


		controller = new NotificationController(executorService, sseNotificationManager, notificationService, notificationsProperties);

		// When
		var resp = controller.markAllAsRead(user);

		// Then
		verify(notificationService, times(1)).markAllAsRead(8L);
		assertEquals(204, resp.getStatusCode().value());
	}

	@Test
	void pollAndSendNotifications_sendsNotificationsAndHeartbeats() {
		// Given
		when(sseNotificationManager.getUserIds()).thenReturn(Set.of(1L, 2L));

		Notification n1 = Instancio.of(Notification.class)
				.set(field(Notification::getId), 555L)
				.set(field(Notification::getUserId), 1L)
				.set(field(Notification::getType), "T")
				.set(field(Notification::getContent), "{}")
				.set(field(Notification::getCreatedAt), LocalDateTime.now())
				.set(field(Notification::getIsRead), false)
				.create();

		when(notificationService.getUnreadByUserIds(Set.of(1L, 2L))).thenReturn(List.of(n1));

		// make executor run submitted tasks synchronously
		when(executorService.submit(any(Runnable.class))).thenAnswer(inv -> {
			Runnable r = inv.getArgument(0);
			r.run();
			return java.util.concurrent.CompletableFuture.completedFuture(null);
		});


		controller = new NotificationController(executorService, sseNotificationManager, notificationService, notificationsProperties);

		// When
		controller.pollAndSendNotifications();

		// Then
		verify(sseNotificationManager, atLeastOnce()).sendNotification(555L);
		verify(sseNotificationManager, atLeastOnce()).sendHeartbeat(2L);
	}

}





