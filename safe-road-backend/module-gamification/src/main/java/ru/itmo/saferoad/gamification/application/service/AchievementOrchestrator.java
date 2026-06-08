package ru.itmo.saferoad.gamification.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.gamification.application.service.evaluator.AchievementEvaluatorRegistry;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.domain.AchievementRequirements;
import ru.itmo.saferoad.gamification.domain.ActionType;
import ru.itmo.saferoad.gamification.domain.repository.AchievementRepository;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Achievement orchestrator: evaluates database-driven achievements using strategy evaluators.
 */

/**
 * Оркестратор для обработки достижений.
 * <p>
 * Отвечает за проверку наличия условий для разблокировки достижений
 * на основе объединенной информации о завершенном тесте.
 * <p>
 * На данный момент это заглушка, которую можно расширять по необходимости.
 * В будущем здесь может быть:
 * - Проверка условий достижений (badges)
 * - Отправка уведомлений о новых достижениях
 * - Логирование всех триггеров достижений
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementOrchestrator {

	private final AchievementRepository achievementRepository;
	private final AchievementEvaluatorRegistry evaluatorRegistry;
	private final AchievementEngine achievementEngine;
	private final JsonMapper jsonMapper;

	/**
	 * Обрабатывает потенциальные триггеры достижений.
	 *
	 * @param userId идентификатор пользователя
	 */
	@Transactional
	public void processTriggers(Long userId) {
		List<Achievement> candidates = achievementRepository.findCandidateAchievements(userId);

		for (Achievement ach : candidates) {
			AchievementRequirements req = ach.getRequirements();
			ActionType actionType = req.getActionType();
			var evaluator = evaluatorRegistry.getEvaluatorsByAction().get(actionType);
			if (evaluator == null) {
				continue;
			}

			boolean met = evaluator.isThresholdMet(userId, req);
			if (met) {
				achievementEngine.awardAchievement(userId, ach);
			}
		}
	}
}

