package ru.itmo.saferoad.auth.dto.avatar;

public record ProfileAvatarResponse(
		Long userId,
		Integer avatarId,
		String avatarName,
		String avatarUrl
) {
}

