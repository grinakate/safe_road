package ru.itmo.saferoad.gamification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.UserMetric;
import ru.itmo.saferoad.gamification.domain.UserMetricId;

import java.util.List;

public interface UserMetricRepository extends JpaRepository<UserMetric, UserMetricId> {
	List<UserMetric> findByUserId(Long userId);
}
