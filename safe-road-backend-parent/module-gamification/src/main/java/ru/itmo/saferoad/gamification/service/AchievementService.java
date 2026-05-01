package ru.itmo.saferoad.gamification.service;

import lombok.NonNull;
import ru.itmo.saferoad.gamification.domain.Achievement;

import java.util.List;

public interface AchievementService {

	@NonNull
	List<Achievement> getActiveAchievements();

	void evaluateAndGrantLevelAchievements(@NonNull Long userId, @NonNull Integer levelNumber);
}
