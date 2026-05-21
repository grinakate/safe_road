package ru.itmo.saferoad.learning.dto;

public record StartTestRequest(
		Integer sectionId,
		Integer topicId,
		String mode
) {
}
