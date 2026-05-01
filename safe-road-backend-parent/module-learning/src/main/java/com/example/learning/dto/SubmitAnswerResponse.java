package com.example.learning.dto;

public record SubmitAnswerResponse(
		boolean correct,
		int awardedXp
) {
}

