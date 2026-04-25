package ru.itmo.saferoad.domain.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.saferoad.domain.entity.User;
import ru.itmo.saferoad.domain.entity.UserVerificationToken;

import java.util.List;
import java.util.Optional;

public interface UserVerificationTokenRepository extends JpaRepository<UserVerificationToken, Long> {

	@NotNull
	Optional<UserVerificationToken> findByToken(@NotNull String token);

	@NotNull
	List<UserVerificationToken> findByUser(@NotNull User user);
}
