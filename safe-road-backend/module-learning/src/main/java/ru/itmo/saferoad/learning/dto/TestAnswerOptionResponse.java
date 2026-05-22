package ru.itmo.saferoad.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestAnswerOptionResponse {
    private Integer id;
    private String text;
    private String feedback;
    private Boolean isCorrect;
}
