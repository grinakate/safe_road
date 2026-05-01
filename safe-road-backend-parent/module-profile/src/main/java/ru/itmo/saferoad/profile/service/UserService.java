package ru.itmo.saferoad.profile.service;

import lombok.NonNull;
import ru.itmo.saferoad.profile.domain.User;

import java.util.Optional;
import java.util.List;

public interface UserService {

	@NonNull
	User save(@NonNull User user);

	boolean existsByEmail(@NonNull String email);

	@NonNull
	Optional<User> findByEmail(@NonNull String email);

	@NonNull
	User existingById(@NonNull Long id);

	@NonNull
	List<User> getTopUsersByXp();

	@NonNull
	User changeAvatar(@NonNull Long userId, @NonNull Integer avatarId);
}
