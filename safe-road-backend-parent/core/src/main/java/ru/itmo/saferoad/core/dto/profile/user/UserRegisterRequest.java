package ru.itmo.saferoad.core.dto.profile.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegisterRequest {

	@NotNull
	private String name;

	@Email
	@NotNull
	private String email;

	@NotNull
	private LocalDate birthDate;

	@NotNull
	private String password;

	@NotNull
	private Integer avatarId;
}
