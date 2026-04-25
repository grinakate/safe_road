package ru.itmo.saferoad.app.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LevelResponse {

	@NotNull
	private Integer number;

	@NotNull
	private String title;

	@NotNull
	private Integer xpThreshold;
}
