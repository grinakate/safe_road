package ru.itmo.saferoad.gamification.service.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.domain.XpHistory;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;
import ru.itmo.saferoad.gamification.domain.repository.XpHistoryRepository;
import ru.itmo.saferoad.gamification.service.XpHistoryService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@AllArgsConstructor
public class XpHistoryServiceImpl implements XpHistoryService {

	private final XpHistoryRepository repository;

	@Override
	public @NonNull List<LeaderboardProjection> getTop10ForCurrentWeek() {
		LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		return repository.findWeekLeaderboardProjection(weekStart, Pageable.ofSize(10));
	}

	@Override
	public long getWeeklyRank(long xp) {
		LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		return repository.countByWeekStartAndXpGreaterThan(weekStart, xp);
	}

	@Override
	public long getWeekXpByUser(long userId) {
		LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		return repository.findByUserIdAndWeekStart(userId, weekStart).map(XpHistory::getXp).orElse(0L);
	}

	@Override
	public List<XpHistory> findAllByUserId(@NonNull Long userId) {
		return repository.findAllByUserId(userId);
	}
}
