package ru.itmo.saferoad.gamification.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class XpHistoryDto {

	@NotNull
	private LocalDate weekStart;

	@NotNull
	private Long xp;
}
