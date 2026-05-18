package ru.itmo.saferoad.notifications.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.domain.repository.NotificationRepository;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;

	@Override
	public @NonNull List<Notification> getByUserId(@NonNull Long userId) {
		return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
	}

	@Override
	public @NonNull List<Notification> getUnreadByUserId(@NonNull Long userId) {
		return notificationRepository.findByUserIdInAndIsReadFalse(Set.of(userId));
	}

	@Override
	public List<Long> getUnreadIdsByUserIds(@NonNull Set<Long> userIds) {
		return notificationRepository.findIdsByUserIdInAndIsReadFalse(userIds);
	}

	@Override
	public @NonNull Notification create(@NonNull Long userId, @NonNull NotificationType type, @NonNull String content) {

		Notification notification = new Notification();
		notification.setUserId(userId);
		notification.setType(type.name());
		notification.setContent(content);
		notification.setCreatedAt(LocalDateTime.now());
		notification.setIsRead(false);

		return notificationRepository.save(notification);
	}

	@Override
	public void markAsRead(@NonNull Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new IllegalArgumentException("Notification with id " + notificationId + " does not exist"));
		notification.setIsRead(true);
		notificationRepository.save(notification);
	}

	@Override
	public void markAllAsRead(@NonNull Long userId) {
		List<Notification> unreadNotifications = notificationRepository.findByUserIdInAndIsReadFalse(Set.of(userId));
		unreadNotifications.forEach(n -> n.setIsRead(true));
		notificationRepository.saveAll(unreadNotifications);
	}

	@Override
	public long getUnreadCount(@NonNull Long userId) {
		return notificationRepository.countByUserIdAndIsReadFalse(userId);
	}

	@Override
	public @NonNull Notification save(@NonNull Notification notification) {
		return notificationRepository.save(notification);
	}

	@NonNull
	@Override
	public Notification existingByIdAndLock(@NonNull Long notificationId) {
		return notificationRepository.findByIdAndLock(notificationId).orElseThrow(
				() -> new IllegalArgumentException("Notification with id " + notificationId + " does not exist")
		);
	}
}

