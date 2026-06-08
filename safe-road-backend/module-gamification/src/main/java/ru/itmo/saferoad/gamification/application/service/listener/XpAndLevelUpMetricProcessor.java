package ru.itmo.saferoad.gamification.application.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.core.event.dto.NotificationEvent;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;
import ru.itmo.saferoad.core.event.dto.notifications.RewardsEarnedEventData;
import ru.itmo.saferoad.gamification.config.GamificationProperties;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.Level;
import ru.itmo.saferoad.gamification.domain.event.TestCompletedDomainEvent;
import ru.itmo.saferoad.gamification.domain.repository.GameProfileRepository;
import ru.itmo.saferoad.gamification.domain.repository.LevelRepository;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.Optional;

/**
 * Обработчик доменного события TestCompletedDomainEvent.
 * Отвечает за расчет XP с учетом текущей серии дней, обновление уровня и уведомление пользователя о полученных наградах.
 * <p>
 * Порядок выполнения: @Order(2) - ВТОРОЙ слушатель
 * Гарантирует, что данные серии дней уже обновлены в БД от ActivityStreakMetricProcessor.
 * Благодаря L1-кэшу Hibernate, получит актуальный профиль с новой серией дней.
 * <p>
 * Single Responsibility: обновление XP, логики уровнезации и отправка уведомлений.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XpAndLevelUpMetricProcessor {

	private final JsonMapper jsonMapper;
	private final GameProfileService gameProfileService;
	private final GameProfileRepository gameProfileRepository;
	private final LevelRepository levelRepository;
	private final GamificationProperties gamificationProperties;
	private final CreatePendingEventsService createPendingEventsService;

	@Order(2)
	@EventListener
	@Transactional
	public void onTestCompleted(TestCompletedDomainEvent event) {
		Long userId = event.getUserId();
		TestSessionCompletedEvent payload = event.getPayload();
		GameProfile profile = gameProfileService.existingByUserId(userId);

		int earnedXp = calculateEarnedXp(payload);

		if (earnedXp == 0) {
			return;
		}

		int previousLevelNumber = profile.getLevel().getNumber();

		int newXp = profile.getXp() + earnedXp;
		Level currentLevel = profile.getLevel();

		// Проверяем повышение уровня
		while (newXp >= currentLevel.getXpThreshold()) {
			int nextNumber = currentLevel.getNumber() + 1;
			Optional<Level> nextLevelOpt = levelRepository.findByNumber(nextNumber);
			if (nextLevelOpt.isPresent()) {
				currentLevel = nextLevelOpt.get();
			} else {
				// Если нет следующего уровня, останавливаемся на текущем
				newXp = currentLevel.getXpThreshold();
				break;
			}
		}

		profile.setXp(newXp);
		profile.setLevel(currentLevel);
		gameProfileRepository.save(profile);

		// Отправляем уведомление о полученных наградах
		boolean leveledUp = currentLevel.getNumber() > previousLevelNumber;
		publishRewardsNotification(userId, earnedXp, newXp, leveledUp, currentLevel.getNumber());
	}

	/**
	 * Расчет заработанного XP на основе деталей теста.
	 *
	 * @param payload событие о завершении теста
	 * @return сумма заработанного XP
	 */
	private int calculateEarnedXp(TestSessionCompletedEvent payload) {
		int earnedXp = 0;
		if (payload.getDetails() != null) {
			int baseXp = gamificationProperties.getBaseXpPerQuestion();
			Map<String, Double> coeffs = gamificationProperties.getQuestionTypeCoefficients();

			for (TestSessionCompletedEvent.QuestionDetail detail : payload.getDetails()) {
				if (Boolean.TRUE.equals(detail.getIsCorrect())) {
					int difficulty = detail.getDifficulty() != null ? detail.getDifficulty() : 1;
					double coeff = coeffs.getOrDefault(detail.getType(), 1.0);
					earnedXp += (int) Math.round(baseXp * difficulty * coeff);
				}
			}
		}
		return earnedXp;
	}

	/**
	 * Публикует событие уведомления о полученных наградах.
	 */
	private void publishRewardsNotification(Long userId, int earnedXp, int totalXp, boolean levelUp, int newLevel) {
		RewardsEarnedEventData data = RewardsEarnedEventData.builder()
				.earnedXp(earnedXp)
				.totalXp(totalXp)
				.levelUp(levelUp)
				.newLevel(newLevel)
				.build();

		NotificationEvent notification = NotificationEvent.builder()
				.userId(userId)
				.type(NotificationType.REWARDS_EARNED)
				.data(jsonMapper.writeValueAsString(data))
				.build();

		createPendingEventsService.publishCreateNotificationEvent(notification);
	}
}







