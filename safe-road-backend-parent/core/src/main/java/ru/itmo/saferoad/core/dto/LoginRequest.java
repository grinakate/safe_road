package ru.itmo.saferoad.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class LoginRequest {

	@NotNull
	private String email;

	@NotNull
	private String password;
}
