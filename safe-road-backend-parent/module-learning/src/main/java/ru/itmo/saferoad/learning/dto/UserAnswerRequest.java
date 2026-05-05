package ru.itmo.saferoad.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerRequest {

	private long questionId;

	private int selectedAnswerId;
}
