package ru.itmo.saferoad.content.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionTreeDto {

	@NotNull
	private Integer id;

	@NotNull
	private String title;

	@NotNull
	private String description;

	@NotNull
	private String url;

	@NotNull
	private Integer orderIndex;

	@NotNull
	private List<TopicBriefDto> topics;
}

