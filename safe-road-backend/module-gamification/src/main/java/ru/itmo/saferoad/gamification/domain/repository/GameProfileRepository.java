package ru.itmo.saferoad.gamification.domain.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.gamification.domain.GameProfile;

import java.util.List;

public interface GameProfileRepository extends JpaRepository<GameProfile, Long> {

	@NonNull
	@Query(value = "SELECT gp.user_id as userId, gp.current_xp as currentXp, a.url as avatarUrl, u.nickname as nickname "
				   + "FROM game_profiles gp "
				   + "JOIN users u ON u.id = gp.user_id "
				   + "JOIN avatars a ON a.id = gp.avatar_id "
				   + "WHERE gp.is_leaderboard_participant = true "
				   + "ORDER BY gp.current_xp DESC "
				   + "LIMIT 10",
			nativeQuery = true)
	List<LeaderboardProjection> findTop10ByOrderByCurrentXpDesc();

	long countByXpGreaterThan(long xp);
}
