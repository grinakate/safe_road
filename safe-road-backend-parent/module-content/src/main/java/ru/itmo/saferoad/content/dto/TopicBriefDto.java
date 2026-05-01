package ru.itmo.saferoad.content.dto;

public record TopicBriefDto(
		Integer id,
		String name,
		String description,
		Integer orderIndex,
		Integer xpReward
) {
}

