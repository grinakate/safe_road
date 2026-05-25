package ru.itmo.saferoad.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.saferoad.content.domain.QuestionType;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestionResponse {
    private Long id;
    private QuestionType type;
    private String questionText;
    private String imageUrlPlaceholder;
    private List<TestAnswerOptionResponse> options;
    private Integer correctAnswerNumber;
}
