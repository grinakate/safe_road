package ru.itmo.saferoad.learning.dto;

import ru.itmo.saferoad.content.domain.QuestionType;

import java.util.List;

public record TestQuestionResponse(
    Long id,
	QuestionType type,
    String questionText,
    String imageUrlPlaceholder,
    List<TestAnswerOptionResponse> options,
    Integer correctAnswerNumber
) {}
