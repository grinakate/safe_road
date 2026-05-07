package ru.itmo.saferoad.auth.dto.level;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LevelResponse {

	@NotNull
	private Integer number;

	@NotNull
	private String name;

	@NotNull
	private Integer xpThreshold;
}
