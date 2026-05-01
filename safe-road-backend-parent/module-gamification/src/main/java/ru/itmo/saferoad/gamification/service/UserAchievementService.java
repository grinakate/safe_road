package ru.itmo.saferoad.gamification.service;

import lombok.NonNull;
import ru.itmo.saferoad.gamification.domain.Achievement;

import java.util.List;

public interface UserAchievementService {

	@NonNull
	List<Achievement> getAchievementsByUserId(@NonNull Long userId);
}
