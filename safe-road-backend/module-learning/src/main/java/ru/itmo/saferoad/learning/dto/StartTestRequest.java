package ru.itmo.saferoad.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartTestRequest {
	private Integer sectionId;
	private Integer topicId;
	private String mode;
}
