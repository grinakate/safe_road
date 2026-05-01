package com.example.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTopicRequest(
		@NotNull Integer sectionId,
		@NotBlank String name,
		String description,
		@NotNull Integer orderIndex,
		@NotNull Integer xpReward
) {
}

