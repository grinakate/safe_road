package ru.itmo.saferoad.learning.dto;

import ru.itmo.saferoad.core.domain.enums.ProgressStatus;

public record TopicUserMapResponse(
		Integer id,
		String title,
		Integer orderIndex,
		ProgressStatus status
) {
}
