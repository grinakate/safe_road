package ru.itmo.saferoad.profile.dto.avatar;

public record ProfileAvatarResponse(
		Long userId,
		Integer avatarId,
		String avatarName,
		String avatarUrl
) {
}

