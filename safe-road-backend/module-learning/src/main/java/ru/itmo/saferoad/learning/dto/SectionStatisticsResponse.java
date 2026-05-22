package ru.itmo.saferoad.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SectionStatisticsResponse {

	@NotNull
	private Integer sectionId;

	@NotNull
	private String title;

	@NotNull
	private Integer completedTopics;

	@NotNull
	private Integer totalTopics;

	@NotNull
	private List<TopicStatResponse> topicStatistics;
}
