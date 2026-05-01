package ru.itmo.saferoad.gamification.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.domain.ActionType;
import ru.itmo.saferoad.gamification.domain.UserAchievement;
import ru.itmo.saferoad.gamification.domain.repository.AchievementRepository;
import ru.itmo.saferoad.gamification.domain.repository.UserAchievementRepository;
import ru.itmo.saferoad.gamification.service.AchievementService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

	private final AchievementRepository achievementRepository;
	private final UserAchievementRepository userAchievementRepository;

	@Override
	public @NonNull List<Achievement> getActiveAchievements() {
		return achievementRepository.findByIsActiveTrue();
	}

	@Override
	@Transactional
	public void evaluateAndGrantLevelAchievements(@NonNull Long userId, @NonNull Integer levelNumber) {
		for (Achievement achievement : getActiveAchievements()) {
			var requirements = achievement.getRequirements();
			if (requirements.getActionType() != ActionType.LEVEL_REACHED) {
				continue;
			}
			if (levelNumber < requirements.getValue()) {
				continue;
			}
			if (userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement.getId())) {
				continue;
			}

			UserAchievement userAchievement = new UserAchievement();
			userAchievement.setUserId(userId);
			userAchievement.setAchievementId(achievement.getId());
			userAchievement.setEarnedAt(LocalDateTime.now());
			userAchievementRepository.save(userAchievement);
		}
	}
}
