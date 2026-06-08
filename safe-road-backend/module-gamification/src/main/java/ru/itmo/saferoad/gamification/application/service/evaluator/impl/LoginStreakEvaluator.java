package ru.itmo.saferoad.gamification.application.service.evaluator.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.gamification.application.service.evaluator.AchievementEvaluator;
import ru.itmo.saferoad.gamification.domain.AchievementRequirements;
import ru.itmo.saferoad.gamification.domain.ActionType;
import ru.itmo.saferoad.gamification.service.GameProfileService;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginStreakEvaluator implements AchievementEvaluator {

	private final GameProfileService gameProfileService;

	@Override
	public ActionType getSupportedActionType() {
		return ActionType.LOGIN_STREAK;
	}

	@Override
	public boolean isThresholdMet(Long userId, AchievementRequirements requirement) {
		var profile = gameProfileService.existingByUserId(userId);
		int streak = profile.getCurrentStreak();
		return streak >= (Integer) requirement.getValue();
	}
}

