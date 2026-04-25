package ru.itmo.saferoad.app.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.saferoad.app.controller.dto.LoginRequest;
import ru.itmo.saferoad.app.controller.dto.TokenResponse;
import ru.itmo.saferoad.app.controller.dto.UserRegisterRequest;
import ru.itmo.saferoad.app.controller.dto.UserResponse;
import ru.itmo.saferoad.app.mapper.UserMapper;
import ru.itmo.saferoad.app.security.JwtUtils;
import ru.itmo.saferoad.domain.entity.User;
import ru.itmo.saferoad.services.business.UserService;
import ru.itmo.saferoad.services.business.UserVerificationTokenService;
import ru.itmo.saferoad.services.email.AuthEmailService;

@Transactional
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtUtils jwtUtils;
	private final UserMapper userMapper;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final AuthEmailService authEmailService;
	private final UserVerificationTokenService userVerificationTokenService;

	@PostMapping("/register")
	public UserResponse register(@Valid @RequestBody UserRegisterRequest request) {
		if (userService.existByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот Email уже занят");
		}

		User user = userMapper.mapForCreateParticipant(request);
		User savedUser = userService.save(user);

		var verificationToken = userVerificationTokenService.createTokenForUser(savedUser);
		//authEmailService.sendSimpleMail(savedUser.getEmail(), verificationToken.getToken());

		return userMapper.mapToResponse(savedUser);
	}

	@GetMapping("/confirmEmail")
	public ResponseEntity<?> confirmToken(@Valid @NotNull @RequestParam("token") String token) {
		userVerificationTokenService.verifyEmailByToken(token);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
		var userOpt = userService.findByEmail(request.getEmail());
		if (userOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		User user = userOpt.get();
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
		}

		String token = jwtUtils.generateToken(user);
		return ResponseEntity.ok(new TokenResponse(token));
	}
}