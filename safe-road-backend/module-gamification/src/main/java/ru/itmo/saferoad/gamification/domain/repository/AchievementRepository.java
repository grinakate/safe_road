package ru.itmo.saferoad.gamification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itmo.saferoad.gamification.domain.Achievement;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {

	List<Achievement> findByIsActiveTrue();

	@Query(value = "SELECT a.* FROM achievements a " +
				   "WHERE a.is_active = true " +
				   "AND NOT EXISTS (SELECT 1 FROM user_achievements ua " +
				   "WHERE ua.achievement_id = a.id " +
				   "AND ua.user_id = :userId)", nativeQuery = true)
	List<Achievement> findCandidateAchievements(@Param("userId") Long userId);
}
