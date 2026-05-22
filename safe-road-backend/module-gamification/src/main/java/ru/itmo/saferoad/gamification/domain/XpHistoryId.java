package ru.itmo.saferoad.gamification.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XpHistoryId implements Serializable {

	@NonNull
	private Long userId;

	@NonNull
	private LocalDate weekStart;

}

