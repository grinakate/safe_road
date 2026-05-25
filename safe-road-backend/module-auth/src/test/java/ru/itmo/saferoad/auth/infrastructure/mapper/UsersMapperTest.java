package ru.itmo.saferoad.auth.infrastructure.mapper;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.auth.api.dto.RegisterRequest;
import ru.itmo.saferoad.auth.api.dto.UserProfileResponse;
import ru.itmo.saferoad.auth.domain.UserRole;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.core.time.CurrentTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersMapperTest {

	@InjectMocks
	private UsersMapperImpl mapper;
	@Mock
	private CurrentTime currentTime;

	@Test
	void mapToEntity_shouldMapFieldsAndFillDefaults() {
		// Given
		LocalDate birth = LocalDate.of(1990, 5, 6);
		RegisterRequest req = Instancio.of(RegisterRequest.class)
				.set(field(RegisterRequest::getBirthDate), birth)
				.create();
		String passwordHash = Instancio.create(String.class);
		LocalDateTime now = LocalDateTime.of(2024, 12, 10, 12, 0);
		when(currentTime.nowDateTime()).thenReturn(now);

		// When
		Users user = mapper.mapToEntity(req, passwordHash);

		// Then
		assertNotNull(user);
		assertNull(user.getId());
		assertEquals(passwordHash, user.getPasswordHash());
		assertEquals(birth.atStartOfDay(), user.getBirthDate());
		assertEquals(UserRole.USER, user.getRole());
		assertNotNull(user.getCreatedAt());
		assertNotNull(user.getUpdatedAt());
		assertNotNull(user.getLastLoginDate());
		assertEquals(now, user.getCreatedAt());
		assertEquals(now, user.getUpdatedAt());
		assertEquals(now, user.getLastLoginDate());
	}

	@Test
	void mapToProfileResponse_shouldMapBasicFields() {
		// Given
		Long id = Instancio.create(Long.class);
		String email = Instancio.create(String.class);
		String nickname = Instancio.create(String.class);
		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), id)
				.set(field(Users::getEmail), email)
				.set(field(Users::getNickname), nickname)
				.create();

		// When
		UserProfileResponse resp = mapper.mapToProfileResponse(user);

		// Then
		assertNotNull(resp);
		assertEquals(id, resp.getId());
		assertEquals(email, resp.getEmail());
		assertEquals(nickname, resp.getNickname());
		assertEquals(user.getBirthDate(), resp.getBirthDate());
		assertEquals(user.getRole(), resp.getRole());
	}
}

