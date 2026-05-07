package ru.itmo.saferoad.auth.domain.repository;

import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.auth.domain.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

	@NonNull
	Optional<User> findByEmail(@NonNull String email);

	boolean existsByEmail(@NonNull String email);

	@NonNull
	List<User> findTop10ByOrderByCurrentXpDesc();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select u from User u where u.id = :id")
	Optional<User> findByIdAndLock(Long id);
}
