package ru.itmo.saferoad.gamification.service.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;
import ru.itmo.saferoad.gamification.domain.repository.XpHistoryRepository;
import ru.itmo.saferoad.gamification.service.XpHistoryService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class XpHistoryServiceImpl implements XpHistoryService {

	private final XpHistoryRepository repository;

	@Override
	public @NonNull List<LeaderboardProjection> getCurrentTop10() {
		LocalDate today = LocalDate.now();
		LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		return repository.findWeekLeaderboardProjection(weekStart, Pageable.ofSize(10));
	}

	@Override
	public long getWeeklyRank(int xp) {
		LocalDate today = LocalDate.now();
		LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		return repository.countByWeekStartAndXpGreaterThan(weekStart, xp);
	}
}
