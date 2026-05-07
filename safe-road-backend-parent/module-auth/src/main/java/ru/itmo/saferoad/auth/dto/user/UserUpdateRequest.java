package ru.itmo.saferoad.auth.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequest {

	@NotNull
	@NotEmpty
	private String name;

	@NotNull
	private LocalDate birthDate;

	@NotNull
	@Positive
	private Integer avatarId;

	@NotNull
	private String password;
}
