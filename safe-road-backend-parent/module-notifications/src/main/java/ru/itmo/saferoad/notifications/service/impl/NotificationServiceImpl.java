package ru.itmo.saferoad.notifications.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.auth.domain.Account;
import ru.itmo.saferoad.auth.domain.repository.AccountRepository;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.domain.repository.NotificationRepository;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final AccountRepository accountRepository;

	@Override
	public @NonNull List<Notification> getByUserId(@NonNull Long userId) {
		return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
	}

	@Override
	public @NonNull List<Notification> getUnreadByUserId(@NonNull Long userId) {
		return notificationRepository.findByUserIdAndIsReadFalse(userId);
	}

	@Override
	public @NonNull Notification create(@NonNull Long userId, @NonNull String title, @NonNull Map<String, Object> content) {
		Account user = accountRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " does not exist"));
		
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setTitle(title);
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
		List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
		unreadNotifications.forEach(n -> n.setIsRead(true));
		notificationRepository.saveAll(unreadNotifications);
	}

	@Override
	public long getUnreadCount(@NonNull Long userId) {
		return notificationRepository.countByUserIdAndIsReadFalse(userId);
	}
}

