package ru.itmo.saferoad.gamification.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.UserMetric;
import ru.itmo.saferoad.gamification.domain.UserMetricId;

public interface UserMetricRepository extends JpaRepository<UserMetric, UserMetricId> {
  List<UserMetric> findByIdUserId(Long userId);
}
