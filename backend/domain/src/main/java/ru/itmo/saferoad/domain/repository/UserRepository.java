package ru.itmo.saferoad.domain.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(@NotNull String email);

	@NotNull
	Optional<User> findByEmail(@NotNull String email);
}
