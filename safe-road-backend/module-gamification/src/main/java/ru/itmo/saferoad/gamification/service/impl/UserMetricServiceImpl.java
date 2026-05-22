package ru.itmo.saferoad.gamification.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.gamification.domain.UserMetric;
import ru.itmo.saferoad.gamification.domain.XpHistory;
import ru.itmo.saferoad.gamification.domain.repository.UserMetricRepository;
import ru.itmo.saferoad.gamification.domain.repository.XpHistoryRepository;
import ru.itmo.saferoad.gamification.service.UserMetricService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMetricServiceImpl implements UserMetricService {

	private static final String XP_METRIC_CODE = "xp";

	private final UserMetricRepository userMetricRepository;
	private final XpHistoryRepository xpHistoryRepository;

	@Override
	@Transactional
	public int addXp(@NonNull Long userId, int deltaXp) {
		long currentXp = getXp(userId);
		long newXp = currentXp + Math.max(deltaXp, 0);
		upsertXpMetric(userId, newXp);
		int added = Math.max(deltaXp, 0);
		// Update weekly xp history
		if (added > 0) {
			LocalDate today = LocalDate.now();
			LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
			var maybe = xpHistoryRepository.findByUserIdAndWeekStart(userId, weekStart);
			XpHistory h;
			if (maybe.isPresent()) {
				h = maybe.get();
				h.setXp(h.getXp() + (long) added);
			} else {
				h = new XpHistory();
				h.setUserId(userId);
				h.setWeekStart(weekStart);
				h.setXp((long) added);
			}
			h.setUpdatedAt(LocalDateTime.now());
			xpHistoryRepository.save(h);
		}
		return added;
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

	@Override
	public long getRank(@NonNull Long userId) {
		long xp = getXp(userId);
		// count users with XP strictly greater than this user's XP; rank = count + 1
		long higherCount = userMetricRepository.countByMetricCodeAndValueGreaterThan(XP_METRIC_CODE, xp);
		return higherCount + 1;
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
