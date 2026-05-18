package ru.itmo.saferoad.notifications.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.notifications.dto.NotificationDto;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<NotificationDto>> getNotifications(@AuthenticationPrincipal AppUserDetails currentUser) {
		List<NotificationDto> notifications = notificationService.getByUserId(currentUser.getId())
				.stream()
				.map(n -> new NotificationDto(
						n.getId(),
						n.getTitle(),
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
						n.getTitle(),
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

