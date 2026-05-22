package ru.itmo.saferoad.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicBriefDto {
	private Integer id;
	private String title;
	private String content;
	private Integer orderIndex;

	// Lombok-generated getters (getId/getTitle/getOrderIndex/getContent) are used
}

