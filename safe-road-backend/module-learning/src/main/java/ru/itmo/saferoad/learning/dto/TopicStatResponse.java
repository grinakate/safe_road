package ru.itmo.saferoad.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopicStatResponse {

	@NotNull
	private Integer topicId;

	@NotNull
	private String title;

	@NotNull
	private Integer totalQuestions;

	@NotNull
	private Integer correctAnswers;

	@NotNull
	private Integer wrongAnswers;

	@NotNull
	private Integer notShown;

}
