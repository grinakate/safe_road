package ru.itmo.saferoad.gamification.controller;

import ru.itmo.saferoad.core.dto.gamification.AchievementDto;
import ru.itmo.saferoad.core.dto.gamification.LeaderboardEntryDto;
import ru.itmo.saferoad.core.dto.gamification.MyGamificationStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.service.UserAchievementService;
import ru.itmo.saferoad.gamification.service.UserMetricService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gamification")
public class GamificationController {

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

	@GetMapping("/stats/me")
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



