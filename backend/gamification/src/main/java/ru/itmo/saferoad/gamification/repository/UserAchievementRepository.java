package ru.itmo.saferoad.gamification.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.UserAchievement;
import ru.itmo.saferoad.gamification.domain.UserAchievementId;

public interface UserAchievementRepository
    extends JpaRepository<UserAchievement, UserAchievementId> {
  List<UserAchievement> findByIdUserId(Long userId);
}
