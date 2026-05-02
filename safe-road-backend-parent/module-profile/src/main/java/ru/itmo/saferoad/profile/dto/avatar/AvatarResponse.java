package ru.itmo.saferoad.profile.dto.avatar;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvatarResponse {

	@NotNull
	private Integer id;

	@NotNull
	private String name;

	@NotNull
	private String url;
}
