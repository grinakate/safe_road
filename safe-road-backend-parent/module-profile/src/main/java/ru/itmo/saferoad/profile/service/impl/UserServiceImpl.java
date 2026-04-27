package ru.itmo.saferoad.profile.service.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.profile.domain.User;
import ru.itmo.saferoad.profile.domain.repository.UserRepository;
import ru.itmo.saferoad.profile.service.UserService;

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
}
