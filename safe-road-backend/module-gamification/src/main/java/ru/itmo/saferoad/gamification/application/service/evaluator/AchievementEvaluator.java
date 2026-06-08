package ru.itmo.saferoad.gamification.application.service.evaluator;

import ru.itmo.saferoad.gamification.domain.AchievementRequirements;
import ru.itmo.saferoad.gamification.domain.ActionType;

public interface AchievementEvaluator {

	ActionType getSupportedActionType();

	boolean isThresholdMet(Long userId, AchievementRequirements requirement);
}

