package ru.itmo.saferoad.gamification.service;

import lombok.NonNull;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;

import java.time.LocalDate;
import java.util.List;

public interface XpHistoryService {

	@NonNull
	List<LeaderboardProjection> getCurrentTop10();

	long getWeeklyRank(int xp);
}
