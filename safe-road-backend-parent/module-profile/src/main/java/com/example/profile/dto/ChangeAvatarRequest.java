package com.example.profile.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeAvatarRequest(
		@NotNull Integer avatarId
) {
}

