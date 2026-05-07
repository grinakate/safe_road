package ru.itmo.saferoad.auth.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.saferoad.auth.domain.Account;
import ru.itmo.saferoad.auth.api.dto.LoginRequest;
import ru.itmo.saferoad.auth.api.dto.TokenResponse;
import ru.itmo.saferoad.auth.dto.user.UserRegisterRequest;
import ru.itmo.saferoad.auth.dto.user.UserRegisterResponse;
import ru.itmo.saferoad.auth.infrastructure.mapper.AccountMapper;
import ru.itmo.saferoad.auth.infrastructure.security.JwtUtils;
import ru.itmo.saferoad.auth.application.AuthService;

@Transactional
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtils jwtUtils;
	private final AccountMapper userMapper;
	private final AuthService userService;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public UserRegisterResponse register(@Valid @RequestBody UserRegisterRequest request) {
		if (userService.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот Email уже занят");
		}

		Account user = userMapper.mapToEntity(request);
		Account savedUser = userService.save(user);

		String token = jwtUtils.generateToken(user);
		return userMapper.mapToRegisterResponse(savedUser, token);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		var userOpt = userService.findByEmail(request.getEmail());
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		Account user = userOpt.get();
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		String token = jwtUtils.generateToken(user);
		return ResponseEntity.ok(new TokenResponse(token));
	}
}