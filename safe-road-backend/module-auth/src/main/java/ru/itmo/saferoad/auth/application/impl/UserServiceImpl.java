package ru.itmo.saferoad.auth.application.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.auth.application.UserService;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.domain.repository.UsersRepository;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UsersRepository repository;
	private final PasswordEncoder passwordEncoder;

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
	public void update(long userId, String nickname, String password, LocalDate birthDate) {
		var user = existingById(userId);

		if (nickname != null) {
			user.setNickname(nickname);
		}

		if (password != null) {
			user.setPasswordHash(Objects.requireNonNull(passwordEncoder.encode(password)));
		}

		if (birthDate != null) {
			user.setBirthDate(birthDate.atStartOfDay());
		}
	}
}
