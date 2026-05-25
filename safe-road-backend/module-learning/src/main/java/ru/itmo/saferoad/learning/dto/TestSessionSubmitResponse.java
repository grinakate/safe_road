package ru.itmo.saferoad.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSessionSubmitResponse {
    private Integer totalQuestions;
    private Integer correctCount;
    private List<TestErrorResponse> errors;
}
