package ru.itmo.saferoad.gamification.api;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.saferoad.auth.application.UserService;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.api.dto.AvatarDto;
import ru.itmo.saferoad.gamification.api.dto.ChangeAvatarRequest;
import ru.itmo.saferoad.gamification.api.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.api.dto.UpdateProfileRequest;
import ru.itmo.saferoad.gamification.api.dto.UserAchievementsResponse;
import ru.itmo.saferoad.gamification.api.dto.UserGameProfileDto;
import ru.itmo.saferoad.gamification.api.dto.XpHistoryDto;
import ru.itmo.saferoad.gamification.api.mapper.LeaderboardMapper;
import ru.itmo.saferoad.gamification.domain.Avatar;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.LeaderboardPeriod;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;
import ru.itmo.saferoad.gamification.service.AchievementService;
import ru.itmo.saferoad.gamification.service.AvatarService;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import ru.itmo.saferoad.gamification.service.LeaderboardService;
import ru.itmo.saferoad.gamification.service.LevelService;
import ru.itmo.saferoad.gamification.service.XpHistoryService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gamification")
public class GamificationController {

	private final UserService userService;
	private final LevelService levelService;
	private final AvatarService avatarService;
	private final XpHistoryService xpHistoryService;
	private final GameProfileService gameProfileService;
	private final AchievementService achievementService;
	private final LeaderboardMapper leaderboardMapper;
	private final LeaderboardService leaderboardService;

	@GetMapping("/leaderboard")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<@NonNull List<LeaderboardEntryDto>> getLeaderboard(
			@AuthenticationPrincipal AppUserDetails currentUser,
			@NotNull @RequestParam(name = "timeframe") String timeframe) {
		LeaderboardPeriod period = LeaderboardPeriod.valueOf(timeframe.toUpperCase());

		List<LeaderboardProjection> leaders;
		if (LeaderboardPeriod.WEEK.equals(period)) {
			leaders = xpHistoryService.getTop10ForCurrentWeek();
		} else {
			leaders = gameProfileService.getTotalTop10();
		}

		List<LeaderboardEntryDto> response = leaderboardMapper.toDtoList(leaders, currentUser);

		boolean currentInTop = response.stream().anyMatch(LeaderboardEntryDto::getIsCurrentUser);
		if (!currentInTop) {
			response.add(leaderboardService.buildCurrentUserEntry(currentUser, period));
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserGameProfileDto> getMe(
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var gameProfile = gameProfileService.findById(currentUser.getId()).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Игровой профиль не найден")
		);

		var response = UserGameProfileDto.builder()
				.name(currentUser.getNickname())
				.level(gameProfile.getLevel().getNumber())
				.currentXp(gameProfile.getXp())
				.xpProgress(levelService.getXpProgress(gameProfile.getLevel().getNumber(), gameProfile.getXp()))
				.avatar(UserGameProfileDto.UserAvatarDto.builder()
						.id(gameProfile.getAvatar().getId())
						.url(gameProfile.getAvatar().getUrl())
						.build())
				.currentStreak(gameProfile.getCurrentStreak())
				.leaderboardEnabled(gameProfile.getIsLeaderboardParticipant())
				.build();
		return ResponseEntity.ok(response);
	}

	@PostMapping("me/avatar")
	public ResponseEntity<AvatarDto> changeMyAvatar(@AuthenticationPrincipal AppUserDetails currentUser,
													@RequestBody ChangeAvatarRequest request) {
		Avatar newAvatar = avatarService.existingById(request.getAvatarId());
		GameProfile gameProfile = gameProfileService.existingByUserId(currentUser.getId());

		if (newAvatar.getMinLevel() > gameProfile.getLevel().getNumber()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		gameProfile.setAvatar(newAvatar);

		var response = AvatarDto.builder()
				.id(newAvatar.getId())
				.url(newAvatar.getUrl())
				.minLevel(newAvatar.getMinLevel())
				.isAvailable(true).build();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me/achievements")
	public ResponseEntity<List<UserAchievementsResponse>> getMyAchievements(
			@AuthenticationPrincipal AppUserDetails currentUser) {
		var achievements = achievementService.getAchievementsForUser(currentUser.getId());
		return ResponseEntity.ok(achievements);
	}

	@GetMapping("/avatars")
	public ResponseEntity<List<AvatarDto>> getAvatars(@AuthenticationPrincipal AppUserDetails currentUser) {
		var gameProfile = gameProfileService.existingByUserId(currentUser.getId());

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

	@PatchMapping("/settings/leaderboard")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Void> toggleLeaderboard(@AuthenticationPrincipal AppUserDetails currentUser) {
		var profile = gameProfileService.existingByUserId(currentUser.getId());
		profile.setIsLeaderboardParticipant(!profile.getIsLeaderboardParticipant());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/history")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<XpHistoryDto>> getHistory(@AuthenticationPrincipal AppUserDetails currentUser) {
		// Получаем историю начисления очков для пользователя
		var history = xpHistoryService.findAllByUserId(currentUser.getId());
		var response = history.stream()
				.map(xpHistory -> XpHistoryDto.builder()
						.xp(xpHistory.getXp())
						.weekStart(xpHistory.getWeekStart())
						.build())
				.toList();

		return ResponseEntity.ok(response);
	}

	@PostMapping("/admin/levels")
	public ResponseEntity<?> createLevel() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/levels/{id}")
	public ResponseEntity<?> updateLevel(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/admin/levels/{id}/archive")
	public ResponseEntity<?> archiveLevel(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PostMapping("/admin/achievements")
	public ResponseEntity<?> createAchievement() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/achievements/{id}")
	public ResponseEntity<?> updateAchievement(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/admin/achievements/{id}/archive")
	public ResponseEntity<?> archiveAchievement(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PostMapping("/admin/avatars")
	public ResponseEntity<?> createAvatar() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/avatars/{id}")
	public ResponseEntity<?> updateAvatar(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/admin/avatars/{id}/archive")
	public ResponseEntity<?> archiveAvatar(@PathVariable Integer id) {
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

	@PatchMapping("/me/settings")
	public ResponseEntity<?> updateProfileSettings(@AuthenticationPrincipal AppUserDetails currentUser,
												   @RequestBody UpdateProfileRequest request) {

		if (request.getNickname() != null || request.getPassword() != null || request.getBirthDate() != null) {
			userService.update(
					currentUser.getId(),
					request.getNickname(),
					request.getPassword(),
					request.getBirthDate()
			);
		}

		if (request.getLeaderboardEnabled() != null) {
			gameProfileService.setLeaderboardEnabled(currentUser.getId(), request.getLeaderboardEnabled());
		}

		return ResponseEntity.ok().build();
	}


}



