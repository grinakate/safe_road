package ru.itmo.saferoad.gamification.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class ChangeAvatarRequest {

	@NotNull
	private Integer avatarId;
}
