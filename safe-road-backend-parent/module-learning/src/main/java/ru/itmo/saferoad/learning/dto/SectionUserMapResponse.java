package ru.itmo.saferoad.learning.dto;

import java.util.List;

public record SectionUserMapResponse(Integer id,
									 String name,
									 Integer progressPercent,
									 List<TopicUserMapResponse> topics
) {
}