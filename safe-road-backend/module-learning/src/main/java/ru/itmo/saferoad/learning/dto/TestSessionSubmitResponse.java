package ru.itmo.saferoad.learning.dto;

import java.util.List;

public record TestSessionSubmitResponse(
    Integer totalQuestions,
    Integer correctCount,
    List<TestErrorResponse> errors
) {}
