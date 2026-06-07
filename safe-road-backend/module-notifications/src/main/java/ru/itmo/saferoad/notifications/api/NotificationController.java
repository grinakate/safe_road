package ru.itmo.saferoad.notifications.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.notifications.config.NotificationsProperties;
import ru.itmo.saferoad.notifications.dto.NotificationDto;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.util.List;

@Slf4j
@Transactional
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

	private final NotificationService notificationService;
	private final SseNotificationManager sseNotificationManager;
	private final NotificationsProperties notificationsProperties;

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

