package ru.itmo.saferoad.learning.dto;

import ru.itmo.saferoad.learning.domain.ProgressStatus;

public record TopicUserMapResponse(
		Integer id,
		String name,
		Integer orderIndex,
		ProgressStatus status
) {
}
