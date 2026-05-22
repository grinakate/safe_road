package ru.itmo.saferoad.gamification.service;

import jakarta.validation.constraints.NotNull;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;

import java.util.Optional;
import java.util.List;

public interface GameProfileService {

	@NotNull
	Optional<GameProfile> findById(@NotNull Long id);

	@NotNull
	GameProfile existingByUserId(@NotNull Long id);

	@NotNull
	GameProfile create(@NotNull Long userId, @NotNull Boolean isLeaderboardParticipant);

	List<LeaderboardProjection> getTop10ByCurrentXp();

	long getRank(@NotNull Integer xp);
}
