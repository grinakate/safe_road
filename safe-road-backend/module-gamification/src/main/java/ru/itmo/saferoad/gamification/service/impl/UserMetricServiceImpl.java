package ru.itmo.saferoad.gamification.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.gamification.domain.UserMetric;
import ru.itmo.saferoad.gamification.domain.UserMetricId;
import ru.itmo.saferoad.gamification.domain.XpHistory;
import ru.itmo.saferoad.gamification.domain.repository.UserMetricRepository;
import ru.itmo.saferoad.gamification.domain.repository.XpHistoryRepository;
import ru.itmo.saferoad.gamification.service.UserMetricService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMetricServiceImpl implements UserMetricService {

	private static final String XP_METRIC_CODE = "xp";

	private final UserMetricRepository userMetricRepository;
	private final XpHistoryRepository xpHistoryRepository;
	private final CurrentTime currentTime;

	@Override
	@Transactional
	public void addXp(@NonNull Long userId, int deltaXp) {
		if (deltaXp > 0) {
			// Обновляем общую метрику XP пользователя
			long currentTotalXp = getXp(userId);
			long newTotalXp = currentTotalXp + deltaXp;
			upsertUserTotalXpMetric(userId, newTotalXp);

			// Обновляем историю XP за текущую неделю
			updateWeeklyXpHistory(userId, deltaXp);
		}
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
		long higherCount = userMetricRepository.countByMetricCodeAndValueGreaterThan(XP_METRIC_CODE, xp);
		return higherCount + 1;
	}

	private void upsertUserTotalXpMetric(Long userId, long xpValue) {
		UserMetricId metricId = new UserMetricId(userId, XP_METRIC_CODE);
		UserMetric metric = userMetricRepository.findById(metricId)
				.orElseGet(() -> new UserMetric(userId, XP_METRIC_CODE, 0L, currentTime.nowDateTime()));

		metric.setValue(xpValue);
		metric.setUpdatedAt(currentTime.nowDateTime());
		userMetricRepository.save(metric);
	}

	private void updateWeeklyXpHistory(Long userId, int addedXp) {
		LocalDate weekStart = currentTime.weekStart();
		Optional<XpHistory> existingHistory = xpHistoryRepository.findByUserIdAndWeekStart(userId, weekStart);

		XpHistory xpHistory = existingHistory.map(history -> {
			history.setXp(history.getXp() + addedXp);
			return history;
		}).orElseGet(() -> {
			XpHistory newHistory = new XpHistory();
			newHistory.setUserId(userId);
			newHistory.setWeekStart(weekStart);
			newHistory.setXp((long) addedXp);
			return newHistory;
		});

		xpHistory.setUpdatedAt(currentTime.nowDateTime());
		xpHistoryRepository.save(xpHistory);
	}
}
