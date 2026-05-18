package ru.itmo.saferoad.gamification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.controller.dto.AvatarDto;
import ru.itmo.saferoad.gamification.controller.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.controller.dto.MyGamificationStatsDto;
import ru.itmo.saferoad.gamification.controller.dto.UserAchievementsResponse;
import ru.itmo.saferoad.gamification.service.AchievementService;
import ru.itmo.saferoad.gamification.service.AvatarService;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import ru.itmo.saferoad.gamification.service.LevelService;
import ru.itmo.saferoad.gamification.service.UserMetricService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gamification")
public class GamificationController {

	private final LevelService levelService;
	private final UserMetricService userMetricService;
	private final GameProfileService gameProfileService;
	private final AchievementService achievementService;
	private final AvatarService avatarService;

	@GetMapping("/levels")
	public ResponseEntity<?> getLevels() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/achievements")
	public ResponseEntity<?> getAchievements() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/avatars")
	public ResponseEntity<List<AvatarDto>> getAvatars(@AuthenticationPrincipal AppUserDetails currentUser) {
		var gameProfile = gameProfileService.existiongByUserId(currentUser.getId());

		var avatars = avatarService.findAll();
		var avatarsForUser = avatars.stream()
				.map(avatar -> AvatarDto.builder()
						.id(avatar.getId())
						.url(avatar.getUrl())
						.minLevel(avatar.getMinLevel())
						.isAvailable(avatar.getMinLevel() <= gameProfile.getLevel().getNumber()).build())
				.toList();
		return ResponseEntity.ok(avatarsForUser);
	}

	@PatchMapping("/avatars/select")
	public ResponseEntity<?> selectAvatar() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/settings/leaderboard")
	public ResponseEntity<?> toggleLeaderboard() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/history")
	public ResponseEntity<?> getHistory() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	// Admin endpoints
	@PostMapping("/admin/levels")
	public ResponseEntity<?> createLevel() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/levels/{id}")
	public ResponseEntity<?> updateLevel(@org.springframework.web.bind.annotation.PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/admin/levels/{id}/archive")
	public ResponseEntity<?> archiveLevel(@org.springframework.web.bind.annotation.PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PostMapping("/admin/achievements")
	public ResponseEntity<?> createAchievement() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/achievements/{id}")
	public ResponseEntity<?> updateAchievement(@org.springframework.web.bind.annotation.PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/admin/achievements/{id}/archive")
	public ResponseEntity<?> archiveAchievement(@org.springframework.web.bind.annotation.PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PostMapping("/admin/avatars")
	public ResponseEntity<?> createAvatar() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/avatars/{id}")
	public ResponseEntity<?> updateAvatar(@org.springframework.web.bind.annotation.PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/admin/avatars/{id}/archive")
	public ResponseEntity<?> archiveAvatar(@org.springframework.web.bind.annotation.PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/admin/settings")
	public ResponseEntity<?> getAdminSettings() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/settings")
	public ResponseEntity<?> updateAdminSettings() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/leaderboard")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		List<LeaderboardEntryDto> response = userMetricService.getLeaderboardTop10ByXp().stream()
				.map(metric -> new LeaderboardEntryDto(
						metric.getUserId(),
						metric.getValue()
				))
				.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<MyGamificationStatsDto> getMe(
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var gameProfile = gameProfileService.findById(currentUser.getId()).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Игровой профиль не найден")
		);

		var response = new MyGamificationStatsDto(
				currentUser.getUsername(),
				gameProfile.getLevel().getNumber(),
				gameProfile.getCurrentXp(),
				levelService.getXpProgress(gameProfile.getLevel().getNumber(), gameProfile.getCurrentXp()),
				gameProfile.getAvatar().getId(),
				gameProfile.getCurrentStreak()
		);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me/achievements")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<UserAchievementsResponse>> getMyAchievements(
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var achievements = achievementService.getAchievementsForUser(currentUser.getId());
		return ResponseEntity.ok(achievements);
	}
}



