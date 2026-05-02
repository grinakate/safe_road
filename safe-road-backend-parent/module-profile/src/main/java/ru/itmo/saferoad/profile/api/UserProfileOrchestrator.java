package ru.itmo.saferoad.profile.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.api.GamificationService;
import ru.itmo.saferoad.gamification.dto.UserAchievementsResponse;
import ru.itmo.saferoad.learning.api.LearningService;
import ru.itmo.saferoad.learning.dto.SectionStatResponse;
import ru.itmo.saferoad.profile.domain.User;
import ru.itmo.saferoad.profile.dto.user.UserProfileResponse;
import ru.itmo.saferoad.profile.mapper.LevelMapper;
import ru.itmo.saferoad.profile.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileOrchestrator {

	private final LevelMapper levelMapper;
	private final UserService userService;
	private final LearningService learningService;
	private final GamificationService gamificationService;

	public UserProfileResponse getFullProfile(Long userId) {
		User user = userService.existingById(userId);

		// Запрашиваем данные у модуля обучения
		List<SectionStatResponse> stats = learningService.getUserStats(user.getId());

		// Запрашиваем данные у модуля геймификации
		List<UserAchievementsResponse> achievements = gamificationService.getAchievementsForUser(user.getId());

		// Собираем всё в один большой Record
		return new UserProfileResponse(
				user.getName(),
				user.getAvatar().getId(),
				levelMapper.mapToResponse(user.getLevel()),
				user.getCurrentXp(),
				18,
				100,
				stats,
				achievements
		);
	}
}
