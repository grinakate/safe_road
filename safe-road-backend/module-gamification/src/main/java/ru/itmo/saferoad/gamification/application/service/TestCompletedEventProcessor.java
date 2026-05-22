package ru.itmo.saferoad.gamification.application.service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.core.event.EventProcessor;
import ru.itmo.saferoad.core.event.dto.NotificationEvent;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;
import ru.itmo.saferoad.core.event.dto.notifications.RewardsEarnedEventData;
import ru.itmo.saferoad.gamification.config.GamificationProperties;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.Level;
import ru.itmo.saferoad.gamification.domain.repository.GameProfileRepository;
import ru.itmo.saferoad.gamification.domain.repository.LevelRepository;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import ru.itmo.saferoad.gamification.service.UserMetricService;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TestCompletedEventProcessor implements EventProcessor {

	private final JsonMapper jsonMapper;
	private final GameProfileService gameProfileService;
	private final GameProfileRepository gameProfileRepository;
	private final LevelRepository levelRepository;
	private final UserMetricService userMetricService;
	private final GamificationProperties gamificationProperties;
	private final CreatePendingEventsService createPendingEventsService;

	@Override
	public @NonNull EventType getEventType() {
		return EventType.TEST_SESSIONS_COMPLETED;
	}

	@Override
	@Transactional
	public void processEvent(@NonNull PendingEvent event) {
		try {
			TestSessionCompletedEvent payload = jsonMapper.readValue(event.getContent(), TestSessionCompletedEvent.class);
			Long userId = payload.userId();
			GameProfile profile = gameProfileService.existingByUserId(userId);

			int earnedXp = getEarnedXp(payload);

			if (earnedXp > 0) {
				int previousLevelNumber = profile.getLevel().getNumber();

				int newXp = profile.getCurrentXp() + earnedXp;
				Level currentLevel = profile.getLevel();

				while (newXp >= currentLevel.getXpThreshold()) {
					int nextNumber = currentLevel.getNumber() + 1;
					Optional<Level> nextLevelOpt = levelRepository.findByNumber(nextNumber);
					if (nextLevelOpt.isPresent()) {
						currentLevel = nextLevelOpt.get();
					} else {
						newXp = currentLevel.getXpThreshold();
						break;
					}
				}

				profile.setCurrentXp(newXp);
				profile.setLevel(currentLevel);
				gameProfileRepository.save(profile);

				userMetricService.addXp(userId, earnedXp);

				// Подготовить уведомление для Notification модуля
				boolean leveledUp = currentLevel.getNumber() > previousLevelNumber;
				RewardsEarnedEventData data = RewardsEarnedEventData.builder()
						.earnedXp(earnedXp)
						.totalXp(newXp)
						.levelUp(leveledUp)
						.newLevel(currentLevel.getNumber())
						.build();

				NotificationEvent notification = NotificationEvent.builder()
						.userId(userId)
						.type(NotificationType.REWARDS_EARNED)
						.data(jsonMapper.writeValueAsString(data))
						.build();
				createPendingEventsService.publishCreateNotificationEvent(notification);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to process TEST_SESSIONS_COMPLETED event", e);
		}
	}

	private int getEarnedXp(TestSessionCompletedEvent payload) {
		int earnedXp = 0;
		if (payload.details() != null) {
			int baseXp = gamificationProperties.getBaseXpPerQuestion();
			Map<String, Double> coeffs = gamificationProperties.getQuestionTypeCoefficients();

			for (TestSessionCompletedEvent.QuestionDetail detail : payload.details()) {
				if (Boolean.TRUE.equals(detail.isCorrect())) {
					int difficulty = detail.difficulty() != null ? detail.difficulty() : 1;
					double coeff = coeffs.getOrDefault(detail.type(), 1.0);
					earnedXp += (int) Math.round(baseXp * difficulty * coeff);
				}
			}
		}
		return earnedXp;
	}
}
