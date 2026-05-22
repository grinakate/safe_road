package ru.itmo.saferoad.gamification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.UserMetric;
import ru.itmo.saferoad.gamification.domain.UserMetricId;

import java.util.List;
import java.util.Optional;

public interface UserMetricRepository extends JpaRepository<UserMetric, UserMetricId> {
	List<UserMetric> findByUserId(Long userId);

	List<UserMetric> findTop10ByMetricCodeOrderByValueDesc(String metricCode);

	Optional<UserMetric> findByUserIdAndMetricCode(Long userId, String metricCode);

	long countByMetricCodeAndValueGreaterThan(String metricCode, long value);
}
