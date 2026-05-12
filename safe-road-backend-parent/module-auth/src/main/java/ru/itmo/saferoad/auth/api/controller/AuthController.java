package ru.itmo.saferoad.auth.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.auth.api.dto.LoginRequest;
import ru.itmo.saferoad.auth.api.dto.RegisterRequest;
import ru.itmo.saferoad.auth.api.dto.TokenResponse;
import ru.itmo.saferoad.auth.application.AuthService;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.infrastructure.security.JwtUtils;

@Transactional
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/me")
	public ResponseEntity<?> getMe() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/me/profile")
	public ResponseEntity<?> updateProfile() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/me/password")
	public ResponseEntity<?> updatePassword() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	private final JwtUtils jwtUtils;
	private final AuthService userService;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
		/*if (userService.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот Email уже занят");
		}

		Users user = userMapper.mapToEntity(request);
		Users savedUser = userService.save(user);

		return new TokenResponse(jwtUtils.generateToken(user));*/
		// TODO: Implement
		return ResponseEntity.ok().build();
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