package ru.itmo.saferoad.learning.dto;

import ru.itmo.saferoad.core.types.ProgressStatus;

public record TopicUserMapResponse(
		Integer id,
		String title,
		Integer orderIndex,
		ProgressStatus status
) {
}
