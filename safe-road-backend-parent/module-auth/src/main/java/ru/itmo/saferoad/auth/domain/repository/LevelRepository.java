package ru.itmo.saferoad.auth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.auth.domain.Level;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Integer> {

	Optional<Level> findTopByXpThresholdLessThanEqualOrderByXpThresholdDesc(Integer xpThreshold);
}
