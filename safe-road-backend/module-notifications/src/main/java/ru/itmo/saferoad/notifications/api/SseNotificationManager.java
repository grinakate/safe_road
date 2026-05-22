package ru.itmo.saferoad.notifications.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.itmo.saferoad.notifications.domain.Notification;
import ru.itmo.saferoad.notifications.service.NotificationService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class SseNotificationManager {

	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
	private final NotificationService notificationService;

	public void addEmitter(@NotNull Long userId, @NotNull SseEmitter emitter) {
		emitters.put(userId, emitter);
	}

	public void removeEmitter(@NotNull Long userId) {
		emitters.remove(userId);
	}

	@NotNull
	public Set<Long> getUserIds() {
		return emitters.keySet();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void sendNotification(@NotNull Long notificationId) {
		Notification notification = notificationService.existingByIdAndLock(notificationId);

		var isSuccess = sendToLocalEmitter(notification.getUserId(), notification.getType(), notification.getContent());
		if (isSuccess) {
			notification.setIsRead(true);
		}
	}

	private boolean sendToLocalEmitter(Long userId, String title, String payload) {
		SseEmitter emitter = emitters.get(userId);
		if (emitter == null) {
			return false;
		}

		try {
			emitter.send(SseEmitter.event().name(title).data(payload));
			return true;
		} catch (IOException e) {
			emitters.remove(userId);
			return false;
		}
	}

	/**
	 * Send a lightweight heartbeat (comment) to keep the SSE connection alive.
	 * The client ignores empty/comment events, but this resets connection activity
	 * on the server side and prevents emitter timeout.
	 */
	public void sendHeartbeat(@NotNull Long userId) {
		SseEmitter emitter = emitters.get(userId);
		if (emitter == null) return;
		try {
			emitter.send(SseEmitter.event().comment("heartbeat"));
		} catch (IOException e) {
			emitters.remove(userId);
		}
	}
}
