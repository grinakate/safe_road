package ru.itmo.saferoad.profile.domain.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.profile.domain.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

	@NonNull
	Optional<User> findByEmail(@NonNull String email);

	boolean existsByEmail(@NonNull String email);

	@NonNull
	List<User> findTop10ByOrderByCurrentXpDesc();
}
