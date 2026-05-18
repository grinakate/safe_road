package ru.itmo.saferoad.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {

	@NotNull
	private Integer id;

	@NotNull
	private String text;

	@NotNull
	private String feedBack;

	@NotNull
	private Boolean isCorrect;
}
