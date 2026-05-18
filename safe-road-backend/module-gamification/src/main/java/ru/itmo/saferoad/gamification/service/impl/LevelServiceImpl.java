package ru.itmo.saferoad.gamification.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.domain.Level;
import ru.itmo.saferoad.gamification.domain.repository.LevelRepository;
import ru.itmo.saferoad.gamification.service.LevelService;

@Service
@AllArgsConstructor
public class LevelServiceImpl implements LevelService {

	private final LevelRepository repository;

	@NotNull
	@Override
	public Double getXpProgress(@NotNull Integer currentLevelNumber, @NotNull Integer currentXp) {
		Level currentLevel = repository.findByNumber(currentLevelNumber).orElseThrow();
		Level nextLevel = repository.findByNumber(currentLevelNumber + 1).orElse(null);

		if (nextLevel == null) {
			return 1.0;
		}

		return (double) (currentXp - currentLevel.getXpThreshold())
			   / (nextLevel.getXpThreshold() - currentLevel.getXpThreshold());
	}

	@NotNull
	@Override
	public Level existingByNumber(@NotNull Integer number) {
		return repository.findByNumber(number).orElseThrow();
	}
}
