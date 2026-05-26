package ru.itmo.saferoad.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTopicRequest {
	private String name;
	private String content;
	private Integer orderIndex;
	private Boolean isActive;
}

