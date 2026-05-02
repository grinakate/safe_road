package ru.itmo.saferoad.profile.dto.avatar;

import jakarta.validation.constraints.NotNull;

public record ChangeAvatarRequest(
		@NotNull Integer avatarId
) {
}

