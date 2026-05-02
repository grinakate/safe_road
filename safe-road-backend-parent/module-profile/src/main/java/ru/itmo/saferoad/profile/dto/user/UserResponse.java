package ru.itmo.saferoad.profile.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.itmo.saferoad.profile.dto.level.LevelResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ДТО с информацией о пользователе.
 */
@Data
public class UserResponse {

	@NotNull
	private Long id;

	@NotNull
	private String name;

	@NotNull
	private String email;

	@NotNull
	private LocalDate birthDate;

	@NotNull
	private String role;

	@NotNull
	private LocalDateTime createdAt;

	@NotNull
	private Long currentXp;

	@NotNull
	private LevelResponse level;

	@NotNull
	private String avatarUrl;
}
