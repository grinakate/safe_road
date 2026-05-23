package ru.itmo.saferoad.gamification.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.repository.GameProfileRepository;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;
import ru.itmo.saferoad.gamification.service.AvatarService;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import ru.itmo.saferoad.gamification.service.LevelService;

import java.util.Optional;
import java.util.List;

@Service
@AllArgsConstructor
public class GameProfileServiceImpl implements GameProfileService {

	private static final Integer START_LEVEL_NUMBER = 1;
	private static final Integer START_AVATAR_ID = 1;

	private final LevelService levelService;
	private final AvatarService avatarService;
	private final GameProfileRepository repository;

	@NotNull
	@Override
	public Optional<GameProfile> findById(@NotNull Long id) {
		return repository.findById(id);
	}

	@Override
	public @NotNull GameProfile existingByUserId(@NotNull Long id) {
		return repository.findById(id).orElseThrow();
	}

	@NotNull
	@Override
	public GameProfile create(@NotNull Long userId, @NotNull Boolean isLeaderboardParticipant) {
		GameProfile gameProfile = new GameProfile();
		gameProfile.setUserId(userId);
		gameProfile.setAvatar(avatarService.existingById(START_AVATAR_ID));
		gameProfile.setLevel(levelService.existingByNumber(START_LEVEL_NUMBER));
		gameProfile.setCurrentStreak(0);
		gameProfile.setXp(0);
		gameProfile.setIsLeaderboardParticipant(isLeaderboardParticipant);
		gameProfile.setTotalActiveDays(0);
		return repository.save(gameProfile);
	}

	@Override
	public List<LeaderboardProjection> getTotalTop10() {
		return repository.findTop10ByOrderByCurrentXpDesc();
	}

	@Override
	public long getRank(@NotNull Long xp) {
		long higher = repository.countByXpGreaterThan(xp);
		return higher + 1;
	}
}
