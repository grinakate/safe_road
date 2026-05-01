package ru.itmo.saferoad.gamification.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.gamification.domain.UserMetric;
import ru.itmo.saferoad.gamification.domain.repository.UserMetricRepository;
import ru.itmo.saferoad.gamification.service.UserMetricService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMetricServiceImpl implements UserMetricService {

	private static final String XP_METRIC_CODE = "xp";

	private final UserMetricRepository userMetricRepository;

	@Override
	@Transactional
	public int addXp(@NonNull Long userId, int deltaXp) {
		long currentXp = getXp(userId);
		long newXp = currentXp + Math.max(deltaXp, 0);
		upsertXpMetric(userId, newXp);
		return Math.max(deltaXp, 0);
	}

	@Override
	public @NonNull List<UserMetric> getUserMetrics(@NonNull Long userId) {
		return userMetricRepository.findByUserId(userId);
	}

	@Override
	public @NonNull List<UserMetric> getLeaderboardTop10ByXp() {
		return userMetricRepository.findTop10ByMetricCodeOrderByValueDesc(XP_METRIC_CODE);
	}

	@Override
	public long getXp(@NonNull Long userId) {
		return userMetricRepository.findByUserIdAndMetricCode(userId, XP_METRIC_CODE)
				.map(UserMetric::getValue)
				.orElse(0L);
	}

	private void upsertXpMetric(Long userId, long xpValue) {
		var metricId = new ru.itmo.saferoad.gamification.domain.UserMetricId(userId, XP_METRIC_CODE);
		UserMetric metric = userMetricRepository.findById(metricId).orElseGet(UserMetric::new);
		metric.setUserId(userId);
		metric.setMetricCode(XP_METRIC_CODE);
		metric.setValue(xpValue);
		metric.setUpdatedAt(LocalDateTime.now());
		userMetricRepository.save(metric);
	}
}
