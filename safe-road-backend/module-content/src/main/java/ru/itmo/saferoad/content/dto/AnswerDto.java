package ru.itmo.saferoad.content.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {

	@NotNull
	private Integer id;

	@NotNull
	private String text;

	@NotNull
	private String feedback;
}

