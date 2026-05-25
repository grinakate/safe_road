package ru.itmo.saferoad.core.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSessionCompletedEvent {
    private Long userId;
    private Integer topicId;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private List<QuestionDetail> details;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDetail {
        private String type;
        private Integer difficulty;
        private Boolean isCorrect;
    }
}
