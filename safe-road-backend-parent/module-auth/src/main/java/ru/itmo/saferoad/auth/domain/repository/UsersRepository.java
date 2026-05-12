package ru.itmo.saferoad.auth.domain.repository;

import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.auth.domain.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

	@NonNull
	Optional<Users> findByEmail(@NonNull String email);

	boolean existsByEmail(@NonNull String email);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select u from Users u where u.id = :id")
	Optional<Users> findByIdAndLock(Long id);
}
