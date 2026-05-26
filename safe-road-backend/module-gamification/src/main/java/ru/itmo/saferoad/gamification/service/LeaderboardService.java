package ru.itmo.saferoad.gamification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.api.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.domain.LeaderboardPeriod;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

	private final GameProfileService gameProfileService;
	private final XpHistoryService xpHistoryService;

	public LeaderboardEntryDto buildCurrentUserEntry(AppUserDetails currentUser, LeaderboardPeriod period) {
		var userGameProfile = gameProfileService.existingByUserId(currentUser.getId());

		long userXp = LeaderboardPeriod.WEEK.equals(period)
				? xpHistoryService.getWeekXpByUser(currentUser.getId())
				: userGameProfile.getXp();

		long userRank = LeaderboardPeriod.WEEK.equals(period)
				? xpHistoryService.getWeeklyRank(userXp)
				: gameProfileService.getRank(userXp);

		return LeaderboardEntryDto.builder()
				.xp(userXp)
				.avatarUrl(userGameProfile.getAvatar().getUrl())
				.rank(userRank)
				.isCurrentUser(true)
				.nickname(currentUser.getNickname()).build();
	}
}


