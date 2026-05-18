package ru.itmo.saferoad.gamification.controller.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AvatarDto {

	@NonNull
	private Integer id;

	@NonNull
	private String url;

	@NonNull
	private Integer minLevel;

	@NonNull
	private Boolean isAvailable;
}
