package ru.itmo.saferoad.profile.dto.user;

import ru.itmo.saferoad.gamification.dto.UserAchievementsResponse;
import ru.itmo.saferoad.learning.dto.SectionStatResponse;
import ru.itmo.saferoad.profile.dto.level.LevelResponse;

import java.util.List;

public record UserProfileResponse(
		String name,
		int avatarId,
		LevelResponse level,
		int currentXp,
		int completedLessons,
		int totalLessons,
		List<SectionStatResponse> sectionStats,
		List<UserAchievementsResponse> achievements
) {}