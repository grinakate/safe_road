package ru.itmo.saferoad.gamification.service;

import lombok.NonNull;
import ru.itmo.saferoad.gamification.domain.XpHistory;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;

import java.util.List;

public interface XpHistoryService {

	@NonNull
	List<LeaderboardProjection> getTop10ForCurrentWeek();

	long getWeeklyRank(long xp);

	long getWeekXpByUser(long userId);

	List<XpHistory> findAllByUserId(@NonNull Long userId);
}
