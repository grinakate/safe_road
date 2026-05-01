package ru.itmo.saferoad.content.dto;

import java.util.List;

public record SectionTreeDto(
		Integer id,
		String name,
		Integer orderIndex,
		List<TopicBriefDto> topics
) {
}

