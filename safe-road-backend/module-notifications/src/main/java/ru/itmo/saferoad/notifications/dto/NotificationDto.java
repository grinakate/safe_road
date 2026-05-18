package ru.itmo.saferoad.notifications.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationDto(
		Long id,
		String title,
		String content,
		LocalDateTime createdAt,
		Boolean isRead
) {
}

