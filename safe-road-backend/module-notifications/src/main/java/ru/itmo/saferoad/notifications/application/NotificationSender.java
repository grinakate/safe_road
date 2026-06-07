package ru.itmo.saferoad.notifications.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.notifications.api.SseNotificationManager;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationSender {

	private final ExecutorService executorService;
	private final NotificationService notificationService;
	private final SseNotificationManager sseNotificationManager;

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

		for (Long userId : connectedUserIds) {
			if (!notificationsByUser.containsKey(userId)) {
				executorService.submit(() -> {
					try {
						sseNotificationManager.sendHeartbeat(userId);
					} catch (Exception e) {
						log.debug("Ошибка при проверки связи с userId {}: {}", userId, e.getMessage());
					}
				});
			}
		}
	}
}
