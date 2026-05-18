package ru.itmo.saferoad.learning.dto;

public record TestErrorResponse(
    String questionText,
    String userAnswerText,
    String correctAnswerText,
	String feedback
) {}
