package ru.itmo.saferoad.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnswerRequest {
	@NotBlank
	private String text;
	@NotNull
	private Boolean isCorrect;
	@NotBlank
	private String feedback;
}

