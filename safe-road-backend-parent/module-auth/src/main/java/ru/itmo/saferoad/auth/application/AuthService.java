package ru.itmo.saferoad.auth.application;

import lombok.NonNull;
import ru.itmo.saferoad.auth.domain.Account;

import java.util.List;
import java.util.Optional;

public interface AuthService {

	@NonNull
	Account save(@NonNull Account user);

	boolean existsByEmail(@NonNull String email);

	@NonNull
	Optional<Account> findByEmail(@NonNull String email);

	@NonNull
	Account existingById(@NonNull Long id);

	@NonNull
	Account existingByIdAndLock(@NonNull Long id);

	@NonNull
	List<Account> getTopUsersByXp();
}
