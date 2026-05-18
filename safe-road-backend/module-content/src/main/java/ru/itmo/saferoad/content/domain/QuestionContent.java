package ru.itmo.saferoad.content.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionContent {

	@NotNull
	@JsonProperty(value = "question_text")
	private String questionText;

	@JsonProperty(value = "image_url_placeholder")
	private String imageUrlPlaceholder;

	@JsonProperty(value = "options")
	private List<AnswerOption> options;

	@Data
	public static class AnswerOption {

		@JsonProperty(value = "number")
		private Integer number;

		@JsonProperty(value = "text")
		private String text;

		@JsonProperty(value = "is_correct")
		private Boolean isCorrect;

		@JsonProperty(value = "feedback")
		private String feedback;
	}
}
