package ru.itmo.saferoad.learning.dto;

public record TestAnswerOptionResponse(
    Integer id,
    String text,
    String feedback,
    Boolean isCorrect
) {}
