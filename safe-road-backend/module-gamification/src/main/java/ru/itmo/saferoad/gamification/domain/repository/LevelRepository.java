package ru.itmo.saferoad.gamification.domain.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.gamification.domain.Level;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Integer> {

	@NotNull
	Optional<Level> findByNumber(@NotNull Integer number);
}
