package ru.itmo.saferoad.gamification.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.core.event.dto.NotificationEvent;
import ru.itmo.saferoad.core.event.dto.notifications.AchievementEarnedEventData;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.domain.UserAchievement;
import ru.itmo.saferoad.gamification.domain.repository.UserAchievementRepository;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementEngine {

	private final JsonMapper jsonMapper;
	private final CurrentTime currentTime;
	private final UserAchievementRepository userAchievementRepository;
	private final CreatePendingEventsService createPendingEventsService;

	@Transactional
	public void awardAchievement(Long userId, Achievement achievement) {
		Integer achievementId = achievement.getId();
		if (userAchievementRepository.existsByUserIdAndAchievementId(userId, achievementId)) {
			return;
		}

		UserAchievement ua = new UserAchievement();
		ua.setUserId(userId);
		ua.setAchievementId(achievementId);
		ua.setEarnedAt(currentTime.nowDateTime());
		userAchievementRepository.save(ua);

		publishRewardsNotification(userId, achievement);
	}

	/**
	 * Публикует событие уведомления о полученных наградах.
	 */
	private void publishRewardsNotification(Long userId, Achievement achievement) {
		AchievementEarnedEventData data = AchievementEarnedEventData.builder()
				.achievementId(achievement.getId())
				.title(achievement.getName())
				.iconUrl(achievement.getIconUrl())
				.earnedXp(achievement.getRewardXp())
				.build();

		NotificationEvent notification = NotificationEvent.builder()
				.userId(userId)
				.type(NotificationType.NEW_ACHIEVEMENT)
				.data(jsonMapper.writeValueAsString(data))
				.build();

		createPendingEventsService.publishCreateNotificationEvent(notification);
	}
}

