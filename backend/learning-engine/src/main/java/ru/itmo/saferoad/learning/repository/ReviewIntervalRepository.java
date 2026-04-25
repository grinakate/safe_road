package ru.itmo.saferoad.learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.learning.domain.ReviewInterval;

public interface ReviewIntervalRepository extends JpaRepository<ReviewInterval, Integer> {}
