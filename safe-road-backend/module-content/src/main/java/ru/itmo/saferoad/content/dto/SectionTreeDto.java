package ru.itmo.saferoad.content.dto;

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
	private Integer id;
	private String title;
	private Integer orderIndex;
	private List<TopicBriefDto> topics;

	// Lombok-generated getters (getId/getTitle/getTopics) are used
}

