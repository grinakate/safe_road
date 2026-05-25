package ru.itmo.saferoad.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {

	private int correctAnswers;

	private int totalQuestions;

	private int awardedExperience;

	private boolean newLevel = false;
}
