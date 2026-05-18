package ru.itmo.saferoad.notifications.service;

import lombok.NonNull;
import ru.itmo.saferoad.notifications.domain.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationService {

	@NonNull
	List<Notification> getByUserId(@NonNull Long userId);

	@NonNull
	List<Notification> getUnreadByUserId(@NonNull Long userId);

	@NonNull
	Notification create(@NonNull Long userId, @NonNull String title, @NonNull String content);

	void markAsRead(@NonNull Long notificationId);

	void markAllAsRead(@NonNull Long userId);

	long getUnreadCount(@NonNull Long userId);
}

