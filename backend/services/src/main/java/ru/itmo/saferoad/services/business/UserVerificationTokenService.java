package ru.itmo.saferoad.services.business;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.domain.entity.User;
import ru.itmo.saferoad.domain.entity.UserVerificationToken;
import ru.itmo.saferoad.domain.repository.UserVerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserVerificationTokenService {

	private UserVerificationTokenRepository repository;

	public void deletePreviousTokensByUser(@NotNull User user) {
		var tokens = repository.findByUser(user);
		repository.deleteAll(tokens);
	}

	@NotNull
	public UserVerificationToken createTokenForUser(@NotNull User user) {
		UserVerificationToken verificationToken = new UserVerificationToken();
		verificationToken.setUser(user);
		verificationToken.setToken(UUID.randomUUID().toString());
		verificationToken.setExpiresAt(LocalDateTime.now().plusDays(1));
		verificationToken.setCreatedAt(LocalDateTime.now());
		return repository.save(verificationToken);
	}

	public boolean verifyEmailByToken(@NotNull String token) {
		UserVerificationToken verificationToken = repository.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid verification token"));

		User user = verificationToken.getUser();

		if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Verification token has expired");
		}

		user.setEmailVerified(true);
		repository.delete(verificationToken);
		return true;
	}
}
