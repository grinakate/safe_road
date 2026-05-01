package com.example.content.dto;

import java.util.List;

public record SectionTreeDto(
		Integer id,
		String name,
		Integer orderIndex,
		List<TopicBriefDto> topics
) {
}

