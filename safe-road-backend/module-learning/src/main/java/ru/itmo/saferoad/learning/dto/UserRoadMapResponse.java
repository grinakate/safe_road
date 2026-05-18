package ru.itmo.saferoad.learning.dto;

import java.util.List;

public record UserRoadMapResponse(Integer id,
								  String title,
								  Integer progressPercent,
								  List<TopicUserMapResponse> topics
) {
}