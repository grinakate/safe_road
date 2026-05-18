package ru.itmo.saferoad.core.event;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class UserRegisteredEvent {

	@NonNull
	private Long userId;

	@NonNull
	private LocalDate birthDate;
}
