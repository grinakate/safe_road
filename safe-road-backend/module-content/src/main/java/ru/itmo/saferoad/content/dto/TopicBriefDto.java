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
public class TopicBriefDto {

	@NotNull
	private Integer id;

	@NotNull
	private String title;

	@NotNull
	private String content;

	@NotNull
	private Integer orderIndex;
}

