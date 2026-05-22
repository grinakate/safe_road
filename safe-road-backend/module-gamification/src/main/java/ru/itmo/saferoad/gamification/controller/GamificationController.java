package ru.itmo.saferoad.gamification.controller;

import lombok.NonNull;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.controller.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.controller.dto.MyGamificationStatsDto;
import ru.itmo.saferoad.gamification.controller.dto.UserAchievementsResponse;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;
import ru.itmo.saferoad.gamification.service.AchievementService;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import ru.itmo.saferoad.gamification.service.LevelService;
import ru.itmo.saferoad.gamification.service.XpHistoryService;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gamification")
public class GamificationController {

	private final LevelService levelService;
	private final XpHistoryService xpHistoryService;
	private final GameProfileService gameProfileService;
	private final AchievementService achievementService;

	@GetMapping("/leaderboard")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<@NonNull List<LeaderboardEntryDto>> getLeaderboard(
			@AuthenticationPrincipal AppUserDetails currentUser,
			@RequestParam(name = "timeframe", required = false, defaultValue = "all") String timeframe
	) {
		List<LeaderboardProjection> leaders;
		if ("week".equalsIgnoreCase(timeframe)) {
			leaders = xpHistoryService.getTop10ForCurrentWeek();
		} else {
			leaders = gameProfileService.getTotalTop10();
		}

		boolean currentInTop = false;
		List<LeaderboardEntryDto> response = new ArrayList<>(11);
		for (int i = 0; i < leaders.size(); i++) {
			var h = leaders.get(i);
			var isCurrentUser = h.getUserId().equals(currentUser.getId());
			response.add(new LeaderboardEntryDto(
					i + 1L,
					h.getAvatarUrl(),
					h.getNickname(),
					h.getCurrentXp(),
					isCurrentUser
			));
			if (isCurrentUser) {
				currentInTop = true;
			}
		}

		if (!currentInTop) {
			GameProfile currentGameProfile = gameProfileService.findById(currentUser.getId()).orElseThrow();
			var xpHistoryForWeek = xpHistoryService.getXpHistoryByUser(currentUser.getId());
			int userXp = Math.toIntExact(!"week".equalsIgnoreCase(timeframe)
					? currentGameProfile.getCurrentXp()
					: xpHistoryForWeek != null ? xpHistoryForWeek.getXp() : 0);
			long userRank = "week".equalsIgnoreCase(timeframe)
					? xpHistoryService.getWeeklyRank(userXp)
					: gameProfileService.getRank(userXp);

			response.add(LeaderboardEntryDto.builder()
					.xp(currentGameProfile.getCurrentXp())
					.avatarUrl(currentGameProfile.getAvatar().getUrl())
					.rank(userRank)
					.isCurrentUser(true)
					.nickname(currentUser.getNickname()).build());
		}

		return ResponseEntity.ok(response);
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

	// ...existing code...

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



