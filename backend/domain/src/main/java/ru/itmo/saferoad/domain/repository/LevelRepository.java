package ru.itmo.saferoad.domain.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.domain.entity.Level;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {

	@NotNull
	Optional<Level> findByNumber(int number);
}
