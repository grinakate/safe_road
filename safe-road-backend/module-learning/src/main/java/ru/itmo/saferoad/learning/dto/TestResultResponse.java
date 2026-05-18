package ru.itmo.saferoad.learning.dto;

public record TestResultResponse(
    Integer correctCount,
    Integer totalQuestions,
    Object details
) {}
