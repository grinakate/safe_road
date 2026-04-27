package ru.itmo.saferoad.core.dto.profile.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

	@NotNull
	private String token;
}
