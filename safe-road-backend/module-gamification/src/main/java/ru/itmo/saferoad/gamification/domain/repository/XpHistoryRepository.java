package ru.itmo.saferoad.gamification.domain.repository;

import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itmo.saferoad.gamification.domain.XpHistory;
import ru.itmo.saferoad.gamification.domain.XpHistoryId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface XpHistoryRepository extends JpaRepository<XpHistory, XpHistoryId> {

	@NonNull
	@Query(value = "SELECT x.user_id as userId, x.xp as xp, gp.avatar_id as avatarId, u.nickname as nickname "
				   + "FROM xp_history x "
				   + "JOIN game_profiles gp ON gp.user_id = x.user_id "
				   + "JOIN users u ON u.id = x.user_id "
				   + "WHERE x.user_id = :userId "
				   + "AND x.week_start = :weekStart "
				   + "ORDER BY x.xp DESC",
			nativeQuery = true)
	Optional<LeaderboardProjection> findByUserIdAndWeekStartForLeaderboard(@NonNull Long userId, @NonNull LocalDate weekStart);
	Optional<XpHistory> findByUserIdAndWeekStart(@NonNull Long userId, @NonNull LocalDate weekStart);

	@NonNull
	@Query(value = "SELECT x.user_id as userId, x.xp as currentXp, a.url as avatarUrl, u.nickname as nickname "
				   + "FROM xp_history x "
				   + "JOIN game_profiles gp ON gp.user_id = x.user_id "
				   + "JOIN users u ON u.id = x.user_id "
				   + "JOIN avatars a ON a.id = gp.avatar_id "
				   + "WHERE x.week_start = :weekStart "
				   + "ORDER BY x.xp DESC",
			nativeQuery = true)
	List<LeaderboardProjection> findWeekLeaderboardProjection(@Param("weekStart") LocalDate weekStart,
															  Pageable pageable);

	long countByWeekStartAndXpGreaterThan(LocalDate weekStart, long xp);

	@NonNull
	Optional<XpHistory> findByUserId(@NonNull Long userId);
}


