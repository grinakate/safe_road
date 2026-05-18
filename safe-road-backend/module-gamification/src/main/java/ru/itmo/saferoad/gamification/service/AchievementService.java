package ru.itmo.saferoad.gamification.service;

import lombok.NonNull;
import ru.itmo.saferoad.gamification.controller.dto.UserAchievementsResponse;
import ru.itmo.saferoad.gamification.domain.Achievement;

import java.util.List;

public interface AchievementService {

	@NonNull
	List<Achievement> getActiveAchievements();

	@NonNull
	List<Achievement> getAll();

	@NonNull
	List<UserAchievementsResponse> getAchievementsForUser(@NonNull Long userId);
}
