package ru.itmo.saferoad.auth.api.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.saferoad.auth.api.dto.ChangePasswordRequest;
import ru.itmo.saferoad.auth.api.dto.LoginRequest;
import ru.itmo.saferoad.auth.api.dto.RegisterRequest;
import ru.itmo.saferoad.auth.api.dto.TokenResponse;
import ru.itmo.saferoad.auth.api.dto.UpdateProfileRequest;
import ru.itmo.saferoad.auth.api.dto.UserProfileResponse;
import ru.itmo.saferoad.auth.application.UserService;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.infrastructure.mapper.UsersMapper;
import ru.itmo.saferoad.auth.infrastructure.security.AppUserDetailsImpl;
import ru.itmo.saferoad.auth.infrastructure.security.JwtUtils;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.core.event.dto.CreateGameProfileEvent;
import ru.itmo.saferoad.core.event.dto.UnlockFirstTopicEvent;
import ru.itmo.saferoad.core.time.CurrentTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private JwtUtils jwtUtils;

	@Mock
	private UserService userService;

	@Mock
	private UsersMapper usersMapper;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private CreatePendingEventsService createPendingEventsService;

	@Mock
	private CurrentTime currentTime;

	@InjectMocks
	private AuthController authController;

	@Test
	void logout_shouldReturnOk() {
		// Given

		// When
		ResponseEntity<?> resp = authController.logout();

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertNull(resp.getBody());
	}

	@Test
	void getMe_shouldReturnProfile() {
		// Given
		long userId = 123L;
		var userDetails = AppUserDetailsImpl.builder()
				.id(userId)
				.email("u@example.com")
				.password("pwd")
				.nickname("nick")
				.authorities(Collections.emptyList())
				.build();

		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), userId)
				.create();

		UserProfileResponse profile = Instancio.create(UserProfileResponse.class);

		when(userService.existingById(userId)).thenReturn(user);
		when(usersMapper.mapToProfileResponse(user)).thenReturn(profile);

		// When
		ResponseEntity<UserProfileResponse> resp = authController.getMe(userDetails);

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertSame(profile, resp.getBody());
		verify(userService, times(1)).existingById(userId);
	}

	@Test
	void updateProfile_shouldModifyAndReturnUpdatedProfile() {
		// Given
		long userId = 10L;
		var userDetails = AppUserDetailsImpl.builder()
				.id(userId)
				.email("a@b.c")
				.password("p")
				.nickname("old")
				.authorities(Collections.emptyList())
				.build();

		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), userId)
				.set(field(Users::getNickname), "old")
				.create();

		UpdateProfileRequest request = Instancio.of(UpdateProfileRequest.class)
				// ensure birthDate is present (Instancio may produce null for some fields)
				.set(field(UpdateProfileRequest::getBirthDate), LocalDate.of(1990, 1, 2))
				.create();

		UserProfileResponse profile = Instancio.create(UserProfileResponse.class);

		when(userService.existingById(userId)).thenReturn(user);
		when(usersMapper.mapToProfileResponse(any())).thenReturn(profile);

		LocalDateTime now = LocalDateTime.now();
		when(currentTime.nowDateTime()).thenReturn(now);

		// When
		ResponseEntity<UserProfileResponse> resp = authController.updateProfile(userDetails, request);

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertSame(profile, resp.getBody());
		assertEquals(request.getNickname(), user.getNickname());
		assertEquals(request.getBirthDate().atStartOfDay(), user.getBirthDate());
		verify(userService, times(1)).save(user);
	}

	@Test
	void updatePassword_shouldReturnBadRequestWhenOldPasswordIncorrect() {
		// Given
		long userId = 5L;
		var userDetails = AppUserDetailsImpl.builder()
				.id(userId)
				.email("x@y.z")
				.password("p")
				.nickname("n")
				.authorities(Collections.emptyList())
				.build();

		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), userId)
				.set(field(Users::getPasswordHash), "hashed")
				.create();

		ChangePasswordRequest request = Instancio.create(ChangePasswordRequest.class);

		when(userService.existingById(userId)).thenReturn(user);
		when(passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())).thenReturn(false);

		// When
		ResponseEntity<?> resp = authController.updatePassword(userDetails, request);

		// Then
		assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
		assertNotNull(resp.getBody());
		verify(userService, never()).save(any());
	}

	@Test
	void updatePassword_shouldSaveWhenOldPasswordMatches() {
		// Given
		long userId = 6L;
		var userDetails = AppUserDetailsImpl.builder()
				.id(userId)
				.email("x@y.z")
				.password("p")
				.nickname("n")
				.authorities(Collections.emptyList())
				.build();

		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), userId)
				.set(field(Users::getPasswordHash), "oldHashed")
				.create();

		ChangePasswordRequest request = Instancio.of(ChangePasswordRequest.class)
				.set(field(ChangePasswordRequest::getOldPassword), "old")
				.set(field(ChangePasswordRequest::getNewPassword), "new")
				.create();

		when(userService.existingById(userId)).thenReturn(user);
		when(passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())).thenReturn(true);
		String encoded = "encoded-" + request.getNewPassword();
		when(passwordEncoder.encode(request.getNewPassword())).thenReturn(encoded);

		LocalDateTime now = LocalDateTime.now();
		when(currentTime.nowDateTime()).thenReturn(now);

		// When
		ResponseEntity<?> resp = authController.updatePassword(userDetails, request);

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertNull(resp.getBody());
		assertEquals(encoded, user.getPasswordHash());
		verify(userService, times(1)).save(user);
	}

	@Test
	void register_shouldThrowConflictWhenEmailExists() {
		// Given
		RegisterRequest request = Instancio.create(RegisterRequest.class);
		when(userService.existsByEmail(request.getEmail())).thenReturn(true);

		// When
		var ex = assertThrows(ResponseStatusException.class, () -> authController.register(request));

		// Then
		assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
	}

	@Test
	void register_shouldCreateUserPublishEventsAndReturnToken() {
		// Given
		RegisterRequest request = Instancio.create(RegisterRequest.class);

		when(userService.existsByEmail(request.getEmail())).thenReturn(false);
		String phash = "phash-" + request.getPassword().hashCode();
		when(passwordEncoder.encode(request.getPassword())).thenReturn(phash);

		Users mapped = Instancio.create(Users.class);
		Users saved = Instancio.of(Users.class)
				.set(field(Users::getId), 77L)
				.set(field(Users::getBirthDate), LocalDateTime.of(1995, 1, 1, 0, 0))
				.create();

		when(usersMapper.mapToEntity(any(RegisterRequest.class), eq(phash))).thenReturn(mapped);
		when(userService.save(mapped)).thenReturn(saved);
		when(jwtUtils.generateToken(saved)).thenReturn("tok-xyz");

		// When
		ResponseEntity<TokenResponse> resp = authController.register(request);

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertNotNull(resp.getBody());
		assertEquals("tok-xyz", resp.getBody().getToken());
		verify(createPendingEventsService, times(1)).publishCreateGameProfileEvent(any(CreateGameProfileEvent.class));
		verify(createPendingEventsService, times(1)).publishUnlockFirstTopicEvent(any(UnlockFirstTopicEvent.class));
	}

	@Test
	void login_shouldReturnUnauthorizedWhenUserNotFound() {
		// Given
		LoginRequest request = Instancio.create(LoginRequest.class);
		when(userService.findByEmail(request.getEmail())).thenReturn(Optional.empty());

		// When
		ResponseEntity<?> resp = authController.login(request);

		// Then
		assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
	}

	@Test
	void login_shouldReturnUnauthorizedWhenPasswordIncorrect() {
		// Given
		LoginRequest request = Instancio.create(LoginRequest.class);
		Users user = Instancio.of(Users.class)
				.set(field(Users::getEmail), request.getEmail())
				.create();

		when(userService.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);

		// When
		ResponseEntity<?> resp = authController.login(request);

		// Then
		assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
	}

	@Test
	void login_shouldReturnTokenWhenCredentialsAreCorrect() {
		// Given
		LoginRequest request = Instancio.of(LoginRequest.class).create();
		Users user = Instancio.of(Users.class).create();
		String token = "some-token";

		when(userService.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
		when(jwtUtils.generateToken(user)).thenReturn(token);

		// When
		ResponseEntity<?> resp = authController.login(request);

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertInstanceOf(TokenResponse.class, resp.getBody());
		assertEquals(token, ((TokenResponse) resp.getBody()).getToken());
	}
}

