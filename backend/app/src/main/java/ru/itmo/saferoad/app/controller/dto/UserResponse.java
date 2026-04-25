package ru.itmo.saferoad.app.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.itmo.saferoad.domain.enums.UserRole;

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
	private UserRole role;

	@NotNull
	private Boolean emailVerified;

	@NotNull
	private LocalDateTime createdAt;

	@NotNull
	private Long currentXp;

	@NotNull
	private LevelResponse level;

	@NotNull
	private Long coins;

	@NotNull
	private String avatarUrl;
}
