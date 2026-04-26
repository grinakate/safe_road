package ru.itmo.saferoad.profile.service;

import lombok.NonNull;
import ru.itmo.saferoad.profile.domain.User;

import java.util.Optional;

public interface UserService {

	@NonNull
	User save(@NonNull User user);

	boolean existsByEmail(@NonNull String email);

	@NonNull
	Optional<User> findByEmail(@NonNull String email);
}
