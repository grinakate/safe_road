package ru.itmo.saferoad.learning.dto;

import java.util.Map;

public record SubmitSessionAnswerRequest(
    Map<Long, Integer> answers // questionId -> answerId
) {}
