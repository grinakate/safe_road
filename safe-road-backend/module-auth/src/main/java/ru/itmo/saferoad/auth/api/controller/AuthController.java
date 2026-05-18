package ru.itmo.saferoad.auth.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.saferoad.auth.api.dto.ChangePasswordRequest;
import ru.itmo.saferoad.auth.api.dto.LoginRequest;
import ru.itmo.saferoad.auth.api.dto.RegisterRequest;
import ru.itmo.saferoad.auth.api.dto.TokenResponse;
import ru.itmo.saferoad.auth.api.dto.UpdateProfileRequest;
import ru.itmo.saferoad.auth.api.dto.UserProfileResponse;
import ru.itmo.saferoad.auth.application.AuthService;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.infrastructure.mapper.UsersMapper;
import ru.itmo.saferoad.auth.infrastructure.security.AppUserDetailsImpl;
import ru.itmo.saferoad.auth.infrastructure.security.JwtUtils;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.core.event.dto.CreateGameProfileEvent;
import ru.itmo.saferoad.core.event.dto.UnlockFirstTopicEvent;

import java.time.LocalDateTime;
import java.util.Objects;

@Transactional
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtils jwtUtils;
	private final AuthService userService;
	private final UsersMapper usersMapper;
	private final PasswordEncoder passwordEncoder;
	private final CreatePendingEventsService createPendingEventsService;

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		// Since we use stateless JWT, logout is handled on client side by deleting the token.
		return ResponseEntity.ok().build();
	}

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponse> getMe(@AuthenticationPrincipal AppUserDetailsImpl userDetails) {
		Users user = userService.existingById(userDetails.getId());
		return ResponseEntity.ok(usersMapper.mapToProfileResponse(user));
	}

	@PatchMapping("/me/profile")
	public ResponseEntity<UserProfileResponse> updateProfile(@AuthenticationPrincipal AppUserDetailsImpl userDetails,
															 @Valid @RequestBody UpdateProfileRequest request) {
		Users user = userService.existingById(userDetails.getId());
		user.setNickname(request.getNickname());
		if (request.getBirthDate() != null) {
			user.setBirthDate(request.getBirthDate().atStartOfDay());
		}
		user.setUpdatedAt(LocalDateTime.now());
		userService.save(user);

		return ResponseEntity.ok(usersMapper.mapToProfileResponse(user));
	}

	@PatchMapping("/me/password")
	public ResponseEntity<?> updatePassword(@AuthenticationPrincipal AppUserDetailsImpl userDetails,
											@Valid @RequestBody ChangePasswordRequest request) {
		Users user = userService.existingById(userDetails.getId());
		if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный старый пароль");
		}
		user.setPasswordHash(Objects.requireNonNull(passwordEncoder.encode(request.getNewPassword())));
		user.setUpdatedAt(LocalDateTime.now());
		userService.save(user);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/register")
	public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
		if (userService.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот Email уже занят");
		}

		String passwordHash = passwordEncoder.encode(request.getPassword());
		Users user = usersMapper.mapToEntity(request, passwordHash);
		Users savedUser = userService.save(user);

		createPendingEventsService.publishCreateGameProfileEvent(
				new CreateGameProfileEvent(savedUser.getId(), savedUser.getBirthDate().toLocalDate()));
		createPendingEventsService.publishUnlockFirstTopicEvent(new UnlockFirstTopicEvent(savedUser.getId()));

		return ResponseEntity.ok(new TokenResponse(jwtUtils.generateToken(savedUser)));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		var userOpt = userService.findByEmail(request.getEmail());
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		Users user = userOpt.get();
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		String token = jwtUtils.generateToken(user);
		return ResponseEntity.ok(new TokenResponse(token));
	}
}