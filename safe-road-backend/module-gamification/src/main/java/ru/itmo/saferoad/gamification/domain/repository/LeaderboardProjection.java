package ru.itmo.saferoad.gamification.domain.repository;

/**
 * Projection for weekly leaderboard row: userId, xp, avatarUrl, nickname
 */
public interface LeaderboardProjection {
	Long getUserId();

	Integer getCurrentXp();

	String getAvatarUrl();

	String getNickname();
}

