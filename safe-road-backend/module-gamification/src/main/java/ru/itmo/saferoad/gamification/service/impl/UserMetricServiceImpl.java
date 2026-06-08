package ru.itmo.saferoad.gamification.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.gamification.domain.UserMetric;
import ru.itmo.saferoad.gamification.domain.UserMetricCode;
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

	private final UserMetricRepository userMetricRepository;
	private final XpHistoryRepository xpHistoryRepository;
	private final CurrentTime currentTime;

	@Override
	@Transactional
	public void addXp(@NonNull Long userId, int deltaXp) {
		if (deltaXp > 0) {
			// Обновляем общую метрику XP пользователя
			increaseUserMetric(userId, deltaXp, UserMetricCode.XP);

			// Обновляем историю XP за текущую неделю
			updateWeeklyXpHistory(userId, deltaXp);
		}
	}

	@Override
	public UserMetric getUserMetric(@NonNull Long userId, @NonNull String metricCode) {
		return userMetricRepository.findByUserIdAndMetricCode(userId, metricCode).orElse(null);
	}

	@Override
	public @NonNull List<UserMetric> getUserMetrics(@NonNull Long userId) {
		return userMetricRepository.findByUserId(userId);
	}

	@Override
	public @NonNull List<UserMetric> getLeaderboardTop10ByXp() {
		return userMetricRepository.findTop10ByMetricCodeOrderByValueDesc(UserMetricCode.XP.name());
	}

	@Override
	public long getXp(@NonNull Long userId) {
		return userMetricRepository.findByUserIdAndMetricCode(userId, UserMetricCode.XP.name())
				.map(UserMetric::getValue)
				.orElse(0L);
	}

	@Override
	public long getRank(@NonNull Long userId) {
		long xp = getXp(userId);
		long higherCount = userMetricRepository.countByMetricCodeAndValueGreaterThan(UserMetricCode.XP.name(), xp);
		return higherCount + 1;
	}

	@Override
	public void increaseUserMetric(Long userId, long value, UserMetricCode metricCode) {
		UserMetricId metricId = new UserMetricId(userId, metricCode.name());
		UserMetric metric = userMetricRepository.findById(metricId)
				.orElseGet(() -> new UserMetric(userId, metricCode.name(), 0L, currentTime.nowDateTime()));

		metric.setValue(metric.getValue() + value);
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
