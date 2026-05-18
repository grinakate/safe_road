package ru.itmo.saferoad.gamification.service;

import jakarta.validation.constraints.NotNull;
import ru.itmo.saferoad.gamification.domain.Level;

public interface LevelService {

	@NotNull
	Double getXpProgress(@NotNull Integer currentLevelNumber, @NotNull Integer currentXp);

	@NotNull
	Level existingByNumber(@NotNull Integer number);
}
