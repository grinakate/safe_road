package ru.itmo.saferoad.learning.dto;

public record StartTestRequest(
    Integer topicId,
    String mode
) {}
