package ru.itmo.saferoad.profile.dto;

public record ProfileAvatarResponse(
		Long userId,
		Integer avatarId,
		String avatarName,
		String avatarUrl
) {
}

