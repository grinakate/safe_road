package ru.itmo.saferoad.auth.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.learning.api.LearningService;
import ru.itmo.saferoad.learning.dto.QuizResultResponse;
import ru.itmo.saferoad.learning.dto.QuizSubmissionRequest;
import ru.itmo.saferoad.auth.domain.Avatar;
import ru.itmo.saferoad.auth.domain.Level;
import ru.itmo.saferoad.auth.domain.User;
import ru.itmo.saferoad.auth.dto.avatar.AvatarResponse;
import ru.itmo.saferoad.auth.dto.avatar.ChangeAvatarRequest;
import ru.itmo.saferoad.auth.dto.user.UserProfileResponse;
import ru.itmo.saferoad.auth.dto.user.UserResponse;
import ru.itmo.saferoad.auth.dto.user.UserUpdateRequest;
import ru.itmo.saferoad.auth.mapper.AvatarMapper;
import ru.itmo.saferoad.auth.mapper.UserMapper;
import ru.itmo.saferoad.auth.service.AvatarService;
import ru.itmo.saferoad.auth.service.LevelService;
import ru.itmo.saferoad.auth.service.UserService;

import java.util.Objects;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class UserController {

	private final UserMapper userMapper;
	private final UserService userService;
	private final LevelService levelService;
	private final AvatarMapper avatarMapper;
	private final AvatarService avatarService;
	private final PasswordEncoder passwordEncoder;
	private final LearningService learningService;
	private final UserProfileOrchestrator userProfileOrchestrator;

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal AppUserDetails currentUser) {
		// Здесь будет вызов UserService, который посчитает XP и соберет статистику
		var result = userProfileOrchestrator.getFullProfile(currentUser.getId());
		return ResponseEntity.ok(result);
	}

	@PutMapping("/users/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal AppUserDetails currentUser,
														  @RequestBody UserUpdateRequest request) {
		User user = userService.existingById(currentUser.getId());

		Avatar newAvatar = avatarService.existingById(request.getAvatarId());
		user.setAvatar(newAvatar);
		user.setBirthDate(request.getBirthDate().atStartOfDay());
		user.setName(request.getName());
		user.setPasswordHash(Objects.requireNonNull(passwordEncoder.encode(request.getPassword())));

		userService.save(user);

		var response = userMapper.mapToResponse(user);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping("me/avatar")
	public ResponseEntity<AvatarResponse> changeMyAvatar(
			@AuthenticationPrincipal AppUserDetails currentUser,
			@RequestBody ChangeAvatarRequest request
	) {
		Avatar newAvatar = avatarService.existingById(request.getAvatarId());
		userService.changeAvatar(currentUser.getId(), newAvatar);
		var response = avatarMapper.mapToResponse(newAvatar);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/quiz/submit")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<QuizResultResponse> submit(@Valid @RequestBody QuizSubmissionRequest request,
													 @AuthenticationPrincipal AppUserDetails currentUser) {
		var result = learningService.getQuizResult(request.getAnswers());
		User user = userService.existingByIdAndLock(currentUser.getId());
		user.setCurrentXp(user.getCurrentXp() + result.getAwardedExperience());

		Level currentUserLevel = user.getLevel();
		if (currentUserLevel.getXpThreshold() <= user.getCurrentXp()) {
			user.setLevel(levelService.existingByNumber(currentUserLevel.getNumber() + 1));
			result.setNewLevel(true);
		}

		userService.save(user);

		return ResponseEntity.ok(result);
	}
}
