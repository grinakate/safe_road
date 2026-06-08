package ru.itmo.saferoad.gamification.application.service.evaluator;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.gamification.domain.ActionType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AchievementEvaluatorRegistry {

	@Getter
	private final Map<ActionType, AchievementEvaluator> evaluatorsByAction;

	public AchievementEvaluatorRegistry(List<AchievementEvaluator> evaluators) {
		this.evaluatorsByAction = evaluators.stream()
				.collect(Collectors.toUnmodifiableMap(AchievementEvaluator::getSupportedActionType, e -> e));
	}
}

