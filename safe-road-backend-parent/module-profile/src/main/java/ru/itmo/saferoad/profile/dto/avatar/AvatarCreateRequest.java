package ru.itmo.saferoad.profile.dto.avatar;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvatarCreateRequest {

	@NotNull
	private String name;

	@NotNull
	private String url;
}
