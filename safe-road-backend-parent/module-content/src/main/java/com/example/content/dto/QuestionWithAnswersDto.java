package com.example.content.dto;

import java.util.List;
import java.util.Map;

public record QuestionWithAnswersDto(
		Long id,
		String type,
		Integer difficultyLevel,
		Map<String, Object> content,
		List<AnswerDto> answers
) {
}

