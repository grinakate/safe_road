package ru.itmo.saferoad.notifications.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationDto(
		Long id,
		String title,
		Map<String, Object> content,
		LocalDateTime createdAt,
		Boolean isRead
) {
}

