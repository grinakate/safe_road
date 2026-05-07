package ru.itmo.saferoad.auth.domain.repository;

import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import ru.itmo.saferoad.auth.domain.Account;

import java.util.Optional;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

	@NonNull
	Optional<Account> findByEmail(@NonNull String email);

	boolean existsByEmail(@NonNull String email);

	@NonNull
	List<Account> findTop10ByOrderByCurrentXpDesc();

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select u from Account u where u.id = :id")
	Optional<Account> findByIdAndLock(Long id);
}
