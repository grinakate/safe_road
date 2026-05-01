package com.example.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAnswerRequest(
		@NotBlank String text,
		@NotNull Boolean isCorrect,
		@NotBlank String feedback
) {
}

