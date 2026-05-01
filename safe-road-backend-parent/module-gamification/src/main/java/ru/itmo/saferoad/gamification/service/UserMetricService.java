package ru.itmo.saferoad.gamification.service;

import lombok.NonNull;
import ru.itmo.saferoad.gamification.domain.UserMetric;

import java.util.List;

public interface UserMetricService {

	int addXp(@NonNull Long userId, int deltaXp);

	@NonNull
	List<UserMetric> getUserMetrics(@NonNull Long userId);

	@NonNull
	List<UserMetric> getLeaderboardTop10ByXp();

	long getXp(@NonNull Long userId);
}
