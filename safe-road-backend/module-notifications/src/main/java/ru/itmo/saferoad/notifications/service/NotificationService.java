package ru.itmo.saferoad.notifications.service;

import lombok.NonNull;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;
import ru.itmo.saferoad.notifications.domain.Notification;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NotificationService {

	@NonNull
	List<Notification> getByUserId(@NonNull Long userId);

	@NonNull
	List<Notification> getUnreadByUserId(@NonNull Long userId);

	List<Long> getUnreadIdsByUserIds(@NonNull Set<Long> userIds);

	@NonNull
	Notification create(@NonNull Long userId, @NonNull NotificationType type, @NonNull String content);

	void markAsRead(@NonNull Long notificationId);

	void markAllAsRead(@NonNull Long userId);

	long getUnreadCount(@NonNull Long userId);

	@NonNull
	Notification save(@NonNull Notification notification);

	Notification existingByIdAndLock(@NonNull Long notificationId);
}

