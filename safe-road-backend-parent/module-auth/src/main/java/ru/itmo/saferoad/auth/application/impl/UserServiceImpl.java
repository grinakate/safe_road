package ru.itmo.saferoad.auth.application.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.auth.application.AuthService;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.domain.repository.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements AuthService {

	private final UsersRepository repository;

	@NonNull
	@Override
	public Users save(@NonNull Users user) {
		return repository.save(user);
	}

	@Override
	public boolean existsByEmail(@NonNull String email) {
		return repository.existsByEmail(email);
	}

	@NonNull
	@Override
	public Optional<Users> findByEmail(@NonNull String email) {
		return repository.findByEmail(email);
	}

	@Override
	public @NonNull Users existingById(@NonNull Long id) {
		return repository.findById(id).orElseThrow(
				() -> new IllegalArgumentException("User with id " + id + " does not exist"));
	}

	@Override
	public @NonNull Users existingByIdAndLock(@NonNull Long id) {
		return repository.findByIdAndLock(id).orElseThrow(
				() -> new IllegalArgumentException("User with id " + id + " does not exist")
		);
	}

	@Override
	public @NonNull List<Users> getTopUsersByXp() {
		return repository.findTop10ByOrderByCurrentXpDesc();
	}
}
