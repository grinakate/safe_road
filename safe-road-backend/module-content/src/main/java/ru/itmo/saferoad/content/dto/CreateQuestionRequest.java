package ru.itmo.saferoad.content.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {
	@NotNull
	private Integer topicId;
	@NotBlank
	private String type;
	@NotNull
	private Integer difficultyLevel;
	@NotNull
	private Map<String, Object> content;
	@NotEmpty
	private List<@Valid CreateAnswerRequest> answers;
}

