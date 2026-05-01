package ru.itmo.saferoad.learning.dto;

public record SubmitAnswerResponse(
		boolean correct,
		int awardedXp
) {
}

