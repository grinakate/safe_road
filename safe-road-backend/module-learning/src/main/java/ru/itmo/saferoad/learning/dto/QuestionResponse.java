package ru.itmo.saferoad.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

	@NotNull
	private Long id;

	@NotNull
	private String text;

	@NotNull
	private List<AnswerResponse> options;

	@NotNull
	private Integer correctAnswerId;

}
