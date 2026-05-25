package ru.itmo.saferoad.gamification.service;

import lombok.NonNull;
import ru.itmo.saferoad.gamification.domain.UserMetric;

import java.util.List;

public interface UserMetricService {

	void addXp(@NonNull Long userId, int deltaXp);

	@NonNull
	List<UserMetric> getUserMetrics(@NonNull Long userId);

	@NonNull
	List<UserMetric> getLeaderboardTop10ByXp();

	long getXp(@NonNull Long userId);

	/**
	 * Returns 1-based rank of the given user by XP (1 = top). If user has no XP metric, returns rank for 0 XP.
	 */
	long getRank(@NonNull Long userId);
}
