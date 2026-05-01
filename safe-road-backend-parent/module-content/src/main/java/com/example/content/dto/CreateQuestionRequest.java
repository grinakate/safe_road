package com.example.content.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public record CreateQuestionRequest(
		@NotNull Integer topicId,
		@NotBlank String type,
		@NotNull Integer difficultyLevel,
		@NotNull Map<String, Object> content,
		@NotEmpty List<@Valid CreateAnswerRequest> answers
) {
}

