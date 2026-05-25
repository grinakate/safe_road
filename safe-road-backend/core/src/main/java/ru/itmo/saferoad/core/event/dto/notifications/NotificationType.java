package ru.itmo.saferoad.core.event.dto.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

	REWARDS_EARNED("Награды получены"),
	NEW_ACHIEVEMENT("Новое достижение"),
	SYSTEM_MESSAGE("Системное сообщение");

	private final String title;
}
