package ru.itmo.saferoad.learning.dto;

public record StartTestResponse(
		Long sessionId,
		Integer totalQuestions
) {
}
