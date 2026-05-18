package ru.itmo.saferoad.notifications.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.itmo.saferoad.core.event.EventExecutor;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.notifications.config.NotificationsProperties;
import ru.itmo.saferoad.notifications.dto.NotificationDto;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.util.List;
import java.util.concurrent.ExecutorService;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
	
	private final ExecutorService executorService;
	private final SseNotificationManager sseNotificationManager;
	private final NotificationService notificationService;
	private final NotificationsProperties notificationsProperties;

	@GetMapping("/subscribe")
	public SseEmitter subscribe(@AuthenticationPrincipal AppUserDetails user) {
		Long userId = user.getId();
		SseEmitter emitter = new SseEmitter(notificationsProperties.getSseTimeoutMs());
		sseNotificationManager.addEmitter(userId, emitter);

		emitter.onCompletion(() -> sseNotificationManager.removeEmitter(userId));
		emitter.onTimeout(() -> sseNotificationManager.removeEmitter(userId));

		return emitter;
	}

	@Scheduled(fixedRateString = "${notifications.scheduler-poll-interval-ms:5000}")
	public void pollAndSendNotifications() {
		var connectedUserIds = sseNotificationManager.getUserIds();
		if (connectedUserIds.isEmpty()) {
			return; // Нет подключенных пользователей, нет смысла опрашивать
		}

		// Запрашиваем из БД все НОВЫЕ уведомления для этих пользователей
		List<Long> newNotificationIds = notificationService.getUnreadIdsByUserIds(connectedUserIds);

		for (long notificationId : newNotificationIds) {
			executorService.submit(() -> {
				sseNotificationManager.sendNotification(notificationId);
			});
		}
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<NotificationDto>> getNotifications(@AuthenticationPrincipal AppUserDetails currentUser) {
		List<NotificationDto> notifications = notificationService.getByUserId(currentUser.getId())
				.stream()
				.map(n -> new NotificationDto(
						n.getId(),
						n.getType(),
						n.getContent(),
						n.getCreatedAt(),
						n.getIsRead()
				))
				.toList();
		return ResponseEntity.ok(notifications);
	}

	@GetMapping("/unread")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<NotificationDto>> getUnreadNotifications(
			@AuthenticationPrincipal AppUserDetails currentUser) {
		List<NotificationDto> notifications = notificationService.getUnreadByUserId(currentUser.getId())
				.stream()
				.map(n -> new NotificationDto(
						n.getId(),
						n.getType(),
						n.getContent(),
						n.getCreatedAt(),
						n.getIsRead()
				))
				.toList();
		return ResponseEntity.ok(notifications);
	}

	@GetMapping("/unread-count")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal AppUserDetails currentUser) {
		long count = notificationService.getUnreadCount(currentUser.getId());
		return ResponseEntity.ok(count);
	}

	@PutMapping("/{notificationId}/read")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId,
										   @AuthenticationPrincipal AppUserDetails currentUser) {
		notificationService.markAsRead(notificationId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PutMapping("/mark-all-read")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal AppUserDetails currentUser) {
		notificationService.markAllAsRead(currentUser.getId());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}

