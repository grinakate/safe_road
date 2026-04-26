package ru.itmo.saferoad.gamification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.UserAchievement;
import ru.itmo.saferoad.gamification.domain.UserAchievementId;

import java.util.List;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, UserAchievementId> {
	List<UserAchievement> findByUserId(Long userId);
}
