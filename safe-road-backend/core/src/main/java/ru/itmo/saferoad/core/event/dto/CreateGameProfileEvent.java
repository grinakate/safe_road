package ru.itmo.saferoad.core.event.dto;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class CreateGameProfileEvent {

	@NonNull
	private Long userId;

	@NonNull
	private LocalDate birthDate;
}
