package ru.itmo.saferoad.gamification.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.controller.dto.UserAchievementsResponse;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.domain.repository.AchievementRepository;
import ru.itmo.saferoad.gamification.service.AchievementService;
import ru.itmo.saferoad.gamification.service.UserAchievementService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

	private final AchievementRepository repository;
	private final UserAchievementService userAchievementService;

	@Override
	public @NonNull List<Achievement> getActiveAchievements() {
		return repository.findByIsActiveTrue();
	}

	@Override
	public @NonNull List<Achievement> getAll() {
		return repository.findAll();
	}

	@Override
	public @NonNull List<UserAchievementsResponse> getAchievementsForUser(@NonNull Long userId) {
		List<Achievement> allAchievements = getAll();
		Set<Integer> userAchievementIds = userAchievementService.getAchievementsByUserId(userId)
				.stream()
				.map(Achievement::getId)
				.collect(Collectors.toSet());

		return allAchievements.stream()
				.filter(a -> a.getIsActive() || userAchievementIds.contains(a.getId()))
				.map(a -> new UserAchievementsResponse(
						a.getName(),
						a.getDescription(),
						a.getIconUrl(),
						userAchievementIds.contains(a.getId()),
						a.getRewardXp()
				))
				.sorted(Comparator.comparing(UserAchievementsResponse::isUnlocked).reversed())
				.toList();
	}
}
