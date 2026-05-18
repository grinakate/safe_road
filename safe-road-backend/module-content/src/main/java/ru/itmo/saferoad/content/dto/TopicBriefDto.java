package ru.itmo.saferoad.content.dto;

public record TopicBriefDto(
		Integer id,
		String title,
		String content,
		Integer orderIndex
) {
}

