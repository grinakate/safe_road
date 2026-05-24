package ru.itmo.saferoad.auth.application;

import lombok.NonNull;
import ru.itmo.saferoad.auth.domain.Users;

import java.time.LocalDate;
import java.util.Optional;

public interface UserService {

	@NonNull
	Users save(@NonNull Users user);

	boolean existsByEmail(@NonNull String email);

	@NonNull
	Optional<Users> findByEmail(@NonNull String email);

	@NonNull
	Users existingById(@NonNull Long id);

	@NonNull
	Users existingByIdAndLock(@NonNull Long id);

	void update(long userId, String nickname, String password, LocalDate birthDate);
}
