package ru.itmo.saferoad.gamification.service;

import jakarta.validation.constraints.NotNull;
import ru.itmo.saferoad.gamification.domain.GameProfile;

import java.util.Optional;

public interface GameProfileService {

	@NotNull
	Optional<GameProfile> findById(@NotNull Long id);

	@NotNull
	GameProfile existiongByUserId(@NotNull Long id);

	@NotNull
	GameProfile create(@NotNull Long userId, @NotNull Boolean isLeaderboardParticipant);
}
