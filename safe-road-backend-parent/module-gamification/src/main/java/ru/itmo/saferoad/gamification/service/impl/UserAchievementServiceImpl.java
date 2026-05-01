package ru.itmo.saferoad.gamification.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.domain.repository.AchievementRepository;
import ru.itmo.saferoad.gamification.domain.repository.UserAchievementRepository;
import ru.itmo.saferoad.gamification.service.UserAchievementService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAchievementServiceImpl implements UserAchievementService {

	private final UserAchievementRepository userAchievementRepository;
	private final AchievementRepository achievementRepository;

	@Override
	public @NonNull List<Achievement> getAchievementsByUserId(@NonNull Long userId) {
		List<Integer> achievementIds = userAchievementRepository.findByUserId(userId).stream()
				.map(ua -> ua.getAchievementId())
				.toList();
		if (achievementIds.isEmpty()) {
			return List.of();
		}
		return achievementRepository.findAllById(achievementIds);
	}
}
