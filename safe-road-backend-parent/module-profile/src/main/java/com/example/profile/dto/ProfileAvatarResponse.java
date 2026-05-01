package com.example.profile.dto;

public record ProfileAvatarResponse(
		Long userId,
		Integer avatarId,
		String avatarName,
		String avatarUrl
) {
}

