package ru.itmo.saferoad.notifications.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.notifications.config.NotificationsProperties;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.dto.NotificationDto;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

	private final ExecutorService executorService;
	private final SseNotificationManager sseNotificationManager;
	private final NotificationService notificationService;
	private final NotificationsProperties notificationsProperties;

	public NotificationController(ExecutorService executorService,
								  SseNotificationManager sseNotificationManager,
								  NotificationService notificationService,
								  NotificationsProperties notificationsProperties) {
		this.executorService = executorService;
		this.sseNotificationManager = sseNotificationManager;
		this.notificationService = notificationService;
		this.notificationsProperties = notificationsProperties;
	}

	@GetMapping("/subscribe")
	public SseEmitter subscribe(@AuthenticationPrincipal AppUserDetails user) {
		Long userId = user.getId();
		log.info("SSE subscribe requested for user {}", userId);
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

		// Запрашиваем из БД все НОВЫЕ уведомления для этих пользователей одним запросом
		var allNotifications = notificationService.getUnreadByUserIds(connectedUserIds);

		if (allNotifications.isEmpty()) {
			return;
		}

		Map<Long, List<Notification>> notificationsByUser = allNotifications.stream()
				.collect(Collectors.groupingBy(Notification::getUserId));

		for (var userNotifications : notificationsByUser.entrySet()) {
			var notifications = userNotifications.getValue();
			executorService.submit(() -> {
				for (var n : notifications) {
					try {
						sseNotificationManager.sendNotification(n.getId());
					} catch (Exception ex) {
						log.error("Ошибка во время отправки уведомления с ИД {} пользователю с ИД {}",
								n.getId(), n.getUserId(), ex);
					}
				}
			});
		}

		// For connected users who had no new notifications, send a lightweight heartbeat
		// to keep the SSE connection alive (prevents emitter timeout on idle connections).
		for (Long userId : connectedUserIds) {
			if (!notificationsByUser.containsKey(userId)) {
				executorService.submit(() -> {
					try {
						sseNotificationManager.sendHeartbeat(userId);
					} catch (Exception e) {
						log.debug("Failed to send heartbeat to user {}: {}", userId, e.getMessage());
					}
				});
			}
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
}

