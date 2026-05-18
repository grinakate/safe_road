package ru.itmo.saferoad.core.event.dto;

import java.util.List;

public record TestSessionCompletedEvent(
    Long userId,
    Integer topicId,
    Integer totalQuestions,
    Integer correctAnswers,
    List<QuestionDetail> details
) {
    public record QuestionDetail(
        String type,
        Integer difficulty,
        Boolean isCorrect
    ) {}
}
