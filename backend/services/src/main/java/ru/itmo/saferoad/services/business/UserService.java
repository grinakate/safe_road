package ru.itmo.saferoad.services.business;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.domain.entity.User;
import ru.itmo.saferoad.domain.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository repository;

	public boolean existByEmail(@NotNull String email) {
		return repository.existsByEmail(email);
	}

	@NotNull
	public User save(@NotNull User user) {
		return repository.save(user);
	}

	public Optional<User> findByEmail(@NotNull String email) {
		return repository.findByEmail(email);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
	}
}
