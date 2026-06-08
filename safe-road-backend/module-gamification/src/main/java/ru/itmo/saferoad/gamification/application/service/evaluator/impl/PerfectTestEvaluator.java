package ru.itmo.saferoad.gamification.application.service.evaluator.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.gamification.application.service.evaluator.AchievementEvaluator;
import ru.itmo.saferoad.gamification.domain.AchievementRequirements;
import ru.itmo.saferoad.gamification.domain.ActionType;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerfectTestEvaluator implements AchievementEvaluator {

	@Override
	public ActionType getSupportedActionType() {
		return ActionType.PERFECT_TEST;
	}

	@Override
	public boolean isThresholdMet(Long userId, AchievementRequirements requirement) {
		return false;
	}
}

