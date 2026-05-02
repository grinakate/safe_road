package ru.itmo.saferoad.gamification.api;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.dto.UserAchievementsResponse;
import ru.itmo.saferoad.gamification.service.AchievementService;
import ru.itmo.saferoad.gamification.service.UserAchievementService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GamificationService {

	private final AchievementService achievementService;
	private final UserAchievementService userAchievementService;

	public List<UserAchievementsResponse> getAchievementsForUser(Long userId) {
		List<Achievement> allAchievements = achievementService.getAll();
		Set<Integer> userAchievementIds = userAchievementService.getAchievementsByUserId(userId)
				.stream()
				.map(Achievement::getId)
				.collect(Collectors.toSet());

		return allAchievements.stream()
				.filter(a -> a.getIsActive() || userAchievementIds.contains(a.getId()))
				.map(a -> new UserAchievementsResponse(
						a.getId(),
						a.getName(),
						a.getDescription(),
						a.getIconUrl(),
						userAchievementIds.contains(a.getId())
				))
				.sorted(Comparator.comparing(UserAchievementsResponse::isUnlocked).reversed())
				.toList();
	}
}
