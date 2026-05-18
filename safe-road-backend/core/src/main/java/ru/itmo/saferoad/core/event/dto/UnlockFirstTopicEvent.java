package ru.itmo.saferoad.core.event.dto;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class UnlockFirstTopicEvent {

	@NonNull
	private Long userId;
}
