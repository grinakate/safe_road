package ru.itmo.saferoad.auth.application.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.auth.domain.Account;
import ru.itmo.saferoad.auth.domain.Avatar;
import ru.itmo.saferoad.auth.domain.repository.AccountRepository;
import ru.itmo.saferoad.auth.application.AuthService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements AuthService {

	private final AccountRepository repository;

	@NonNull
	@Override
	public Account save(@NonNull Account user) {
		return repository.save(user);
	}

	@Override
	public boolean existsByEmail(@NonNull String email) {
		return repository.existsByEmail(email);
	}

	@NonNull
	@Override
	public Optional<Account> findByEmail(@NonNull String email) {
		return repository.findByEmail(email);
	}

	@Override
	public @NonNull Account existingById(@NonNull Long id) {
		return repository.findById(id).orElseThrow(
				() -> new IllegalArgumentException("User with id " + id + " does not exist"));
	}

	@Override
	public @NonNull Account existingByIdAndLock(@NonNull Long id) {
		return repository.findByIdAndLock(id).orElseThrow(
				() -> new IllegalArgumentException("User with id " + id + " does not exist")
		);
	}

	@Override
	public @NonNull List<Account> getTopUsersByXp() {
		return repository.findTop10ByOrderByCurrentXpDesc();
	}

	@Override
	public @NonNull Account changeAvatar(@NonNull Long userId, @NonNull Avatar avatar) {
		Account user = existingById(userId);
		if (user.getLevel().getNumber() < avatar.getMinLevel()) {
			throw new IllegalArgumentException("У пользователя недостаточный уровень");
		}

		user.setAvatar(avatar);
		return repository.save(user);
	}
}
