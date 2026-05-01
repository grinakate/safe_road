package com.example.learning.dto;

import jakarta.validation.constraints.NotNull;

public record SubmitAnswerRequest(
		@NotNull Integer answerId
) {
}

