package ru.itmo.saferoad.auth.service.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.auth.domain.Avatar;
import ru.itmo.saferoad.auth.domain.User;
import ru.itmo.saferoad.auth.domain.repository.UserRepository;
import ru.itmo.saferoad.auth.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository repository;

	@NonNull
	@Override
	public User save(@NonNull User user) {
		return repository.save(user);
	}

	@Override
	public boolean existsByEmail(@NonNull String email) {
		return repository.existsByEmail(email);
	}

	@NonNull
	@Override
	public Optional<User> findByEmail(@NonNull String email) {
		return repository.findByEmail(email);
	}

	@Override
	public @NonNull User existingById(@NonNull Long id) {
		return repository.findById(id).orElseThrow(
				() -> new IllegalArgumentException("User with id " + id + " does not exist"));
	}

	@Override
	public @NonNull User existingByIdAndLock(@NonNull Long id) {
		return repository.findByIdAndLock(id).orElseThrow(
				() -> new IllegalArgumentException("User with id " + id + " does not exist")
		);
	}

	@Override
	public @NonNull List<User> getTopUsersByXp() {
		return repository.findTop10ByOrderByCurrentXpDesc();
	}

	@Override
	public @NonNull User changeAvatar(@NonNull Long userId, @NonNull Avatar avatar) {
		User user = existingById(userId);
		if (user.getLevel().getNumber() < avatar.getMinLevel()) {
			throw new IllegalArgumentException("У пользователя недостаточный уровень");
		}

		user.setAvatar(avatar);
		return repository.save(user);
	}
}
