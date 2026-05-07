package ru.itmo.saferoad.auth.dto.avatar;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AvatarCreateRequest {

	@NotNull
	private String name;

	@NotNull
	private String url;

	@NotNull
	@Positive
	private Integer minLevel;
}
