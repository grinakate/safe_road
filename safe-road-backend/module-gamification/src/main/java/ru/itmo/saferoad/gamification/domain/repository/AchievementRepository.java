package ru.itmo.saferoad.gamification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.Achievement;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {

	List<Achievement> findByIsActiveTrue();
}
