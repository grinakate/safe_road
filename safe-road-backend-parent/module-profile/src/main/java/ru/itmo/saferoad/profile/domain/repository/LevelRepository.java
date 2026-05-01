package ru.itmo.saferoad.profile.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.profile.domain.Level;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Integer> {

	Optional<Level> findTopByXpThresholdLessThanEqualOrderByXpThresholdDesc(Integer xpThreshold);
}
