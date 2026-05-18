package ru.itmo.saferoad.core.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;

@Getter
@Builder
@AllArgsConstructor
public class NotificationEvent {
    private final Long userId;
	private final NotificationType type;
    private final String data;
}

