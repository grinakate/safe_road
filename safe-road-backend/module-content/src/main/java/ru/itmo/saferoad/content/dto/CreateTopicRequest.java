package ru.itmo.saferoad.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTopicRequest {
	@NotNull
	private Integer sectionId;
	@NotBlank
	private String name;
	private String description;
	@NotNull
	private Integer orderIndex;
	@NotNull
	private Integer xpReward;
}

