package ru.itmo.saferoad.content.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionContent {

	@NotNull
	@JsonProperty(value = "question_text")
	private String questionText;

	@JsonProperty(value = "image_url_placeholder")
	private String imageUrlPlaceholder;
}
