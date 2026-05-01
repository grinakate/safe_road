package ru.itmo.saferoad.profile.api;

import ru.itmo.saferoad.profile.dto.ChangeAvatarRequest;
import ru.itmo.saferoad.profile.dto.ProfileAvatarResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.core.dto.profile.user.UserResponse;
import ru.itmo.saferoad.core.dto.profile.user.UserUpdateRequest;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.profile.domain.Avatar;
import ru.itmo.saferoad.profile.domain.User;
import ru.itmo.saferoad.profile.mapper.UserMapper;
import ru.itmo.saferoad.profile.service.AvatarService;
import ru.itmo.saferoad.profile.service.UserService;

import java.util.Objects;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class UserController {

	private final UserMapper userMapper;
	private final UserService userService;
	private final AvatarService avatarService;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("users/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal AppUserDetails currentUser) {
		User user = userService.existingById(currentUser.getId());
		var response = userMapper.mapToResponse(user);
		return ResponseEntity.ok().body(response);
	}

	@PutMapping("users/me")
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

	@PutMapping("users/me/avatar")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ProfileAvatarResponse> changeMyAvatar(
			@Valid @RequestBody ChangeAvatarRequest request,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var updatedUser = userService.changeAvatar(currentUser.getId(), request.avatarId());
		var response = new ProfileAvatarResponse(
				updatedUser.getId(),
				updatedUser.getAvatar().getId(),
				updatedUser.getAvatar().getName(),
				updatedUser.getAvatar().getUrl()
		);
		return ResponseEntity.ok(response);
	}
}
