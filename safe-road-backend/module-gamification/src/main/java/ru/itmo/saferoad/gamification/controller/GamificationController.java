package ru.itmo.saferoad.gamification.controller;

import lombok.RequiredArgsConstructor;
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
import ru.itmo.saferoad.core.dto.gamification.AchievementDto;
import ru.itmo.saferoad.core.dto.gamification.LeaderboardEntryDto;
import ru.itmo.saferoad.core.dto.gamification.MyGamificationStatsDto;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.service.UserAchievementService;
import ru.itmo.saferoad.gamification.service.UserMetricService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gamification")
public class GamificationController {

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
	public ResponseEntity<?> getAvatars() {
		// TODO: Implement
		return ResponseEntity.ok().build();
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

	private final UserMetricService userMetricService;
	private final UserAchievementService userAchievementService;

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

	@GetMapping("/status")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<MyGamificationStatsDto> getMyStats(
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var achievements = userAchievementService.getAchievementsByUserId(currentUser.getId()).stream()
				.map(a -> new AchievementDto(a.getId(), a.getName(), a.getDescription(), a.getIconUrl(), a.getRewardXp()))
				.toList();
		var response = new MyGamificationStatsDto(
				currentUser.getId(),
				userMetricService.getXp(currentUser.getId()),
				achievements
		);
		return ResponseEntity.ok(response);
	}
}



