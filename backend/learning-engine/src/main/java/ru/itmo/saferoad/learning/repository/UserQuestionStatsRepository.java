package ru.itmo.saferoad.learning.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;
import ru.itmo.saferoad.learning.domain.UserQuestionStatsId;

public interface UserQuestionStatsRepository
    extends JpaRepository<UserQuestionStats, UserQuestionStatsId> {
  List<UserQuestionStats> findByIdUserId(Long userId);
}
