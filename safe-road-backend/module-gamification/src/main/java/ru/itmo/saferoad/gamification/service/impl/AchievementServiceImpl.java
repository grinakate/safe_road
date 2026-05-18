package ru.itmo.saferoad.gamification.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.domain.repository.AchievementRepository;
import ru.itmo.saferoad.gamification.service.AchievementService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

	private final AchievementRepository achievementRepository;

	@Override
	public @NonNull List<Achievement> getActiveAchievements() {
		return achievementRepository.findByIsActiveTrue();
	}

	@Override
	public @NonNull List<Achievement> getAll() {
		return achievementRepository.findAll();
	}
}
