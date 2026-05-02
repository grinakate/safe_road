package ru.itmo.saferoad.profile.api;

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
import ru.itmo.saferoad.profile.dto.auth.LoginRequest;
import ru.itmo.saferoad.profile.dto.auth.TokenResponse;
import ru.itmo.saferoad.profile.dto.user.UserRegisterRequest;
import ru.itmo.saferoad.profile.dto.user.UserRegisterResponse;
import ru.itmo.saferoad.profile.domain.User;
import ru.itmo.saferoad.profile.mapper.UserMapper;
import ru.itmo.saferoad.profile.security.JwtUtils;
import ru.itmo.saferoad.profile.service.UserService;

@Transactional
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtils jwtUtils;
	private final UserMapper userMapper;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public UserRegisterResponse register(@Valid @RequestBody UserRegisterRequest request) {
		if (userService.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот Email уже занят");
		}

		User user = userMapper.mapToEntity(request);
		User savedUser = userService.save(user);

		String token = jwtUtils.generateToken(user);
		return userMapper.mapToRegisterResponse(savedUser, token);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		var userOpt = userService.findByEmail(request.getEmail());
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		User user = userOpt.get();
		if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		String token = jwtUtils.generateToken(user);
		return ResponseEntity.ok(new TokenResponse(token));
	}
}