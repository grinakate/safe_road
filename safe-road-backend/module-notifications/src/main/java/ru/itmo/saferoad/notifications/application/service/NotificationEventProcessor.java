package ru.itmo.saferoad.notifications.application.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.event.EventProcessor;
import ru.itmo.saferoad.core.event.dto.NotificationEvent;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;
import ru.itmo.saferoad.notifications.service.NotificationService;
import tools.jackson.databind.json.JsonMapper;

@Service
@AllArgsConstructor
public class NotificationEventProcessor implements EventProcessor {

    private final JsonMapper jsonMapper;
    private final NotificationService notificationService;

    @Override
    public @NonNull EventType getEventType() {
        return EventType.CREATE_NOTIFICATION;
    }

    @Override
    @Transactional
    public void processEvent(@NonNull PendingEvent event) {
        try {
            NotificationEvent payload = jsonMapper.readValue(event.getContent(), NotificationEvent.class);
            Long userId = payload.getUserId();
            NotificationType type = payload.getType();
			String content = payload.getData() != null ? payload.getData() : "{}";

            notificationService.create(userId, type, content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CREATE_NOTIFICATION event", e);
        }
    }
}


