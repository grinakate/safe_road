package ru.itmo.saferoad.gamification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {}
