package ru.itmo.saferoad.auth.application.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.domain.repository.UsersRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UsersRepository repository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void save_shouldDelegateToRepository() {
		// Given
		Users user = Instancio.create(Users.class);
		when(repository.save(user)).thenReturn(user);

		// When
		Users result = userService.save(user);

		// Then
		assertSame(user, result);
		verify(repository, times(1)).save(user);
	}

	@Test
	void existsByEmail_shouldReturnRepositoryResult() {
		// Given
		String email = Instancio.create(String.class);
		when(repository.existsByEmail(email)).thenReturn(true);

		// When
		boolean exists = userService.existsByEmail(email);

		// Then
		assertTrue(exists);
		verify(repository, times(1)).existsByEmail(email);
	}

	@Test
	void findByEmail_shouldReturnOptionalFromRepository() {
		// Given
		String email = Instancio.create(String.class);
		Users user = Instancio.of(Users.class)
				.set(field(Users::getEmail), email)
				.create();
		when(repository.findByEmail(email)).thenReturn(Optional.of(user));

		// When
		Optional<Users> opt = userService.findByEmail(user.getEmail());

		// Then
		assertTrue(opt.isPresent());
		assertSame(user, opt.get());
		verify(repository, times(1)).findByEmail(user.getEmail());
	}

	@Test
	void existingById_shouldReturnWhenFound() {
		// Given
		Long id = Instancio.create(Long.class);
		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), id)
				.create();
		when(repository.findById(id)).thenReturn(Optional.of(user));

		// When
		Users result = userService.existingById(id);

		// Then
		assertSame(user, result);
		verify(repository, times(1)).findById(id);
	}

	@Test
	void existingById_shouldThrowWhenNotFound() {
		// Given
		Long id = Instancio.create(Long.class);
		when(repository.findById(id)).thenReturn(Optional.empty());

		// When
		var ex = assertThrows(IllegalArgumentException.class, () -> userService.existingById(id));

		// Then
		assertTrue(ex.getMessage().contains(String.valueOf(id)));
		verify(repository, times(1)).findById(id);
	}

	@Test
	void existingByIdAndLock_shouldReturnWhenFound() {
		// Given
		Long id = Instancio.create(Long.class);
		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), id)
				.create();
		when(repository.findByIdAndLock(id)).thenReturn(Optional.of(user));

		// When
		Users result = userService.existingByIdAndLock(id);

		// Then
		assertSame(user, result);
		verify(repository, times(1)).findByIdAndLock(id);
	}

	@Test
	void update_shouldChangeNicknamePasswordAndBirthDateWhenProvided() {
		// Given
		Long id = Instancio.create(Long.class);
		String oldNick = Instancio.create(String.class);
		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), id)
				.set(field(Users::getNickname), oldNick)
				.create();

		when(repository.findById(id)).thenReturn(Optional.of(user));
		String newPassword = Instancio.create(String.class);
		when(passwordEncoder.encode(newPassword)).thenReturn("encoded-" + newPassword);

		String newNick = Instancio.create(String.class);

		// When
		userService.update(id, newNick, newPassword, LocalDate.of(1995, 2, 3));

		// Then
		assertEquals(newNick, user.getNickname());
		assertEquals("encoded-" + newPassword, user.getPasswordHash());
		assertEquals(LocalDate.of(1995, 2, 3).atStartOfDay(), user.getBirthDate());
		verify(repository, times(1)).findById(id);
		verify(repository, never()).save(any());
	}

	@Test
	void update_shouldNotChangeFieldsWhenNullsProvided() {
		// Given
		Long id = Instancio.create(Long.class);
		Users user = Instancio.of(Users.class)
				.set(field(Users::getId), id)
				.create();

		String originalNick = user.getNickname();
		String originalHash = user.getPasswordHash();
		LocalDateTime originalBirth = user.getBirthDate();

		when(repository.findById(id)).thenReturn(Optional.of(user));

		// When
		userService.update(id, null, null, null);

		// Then
		assertEquals(originalNick, user.getNickname());
		assertEquals(originalHash, user.getPasswordHash());
		assertEquals(originalBirth, user.getBirthDate());
		verify(repository, times(1)).findById(id);
		verify(repository, never()).save(any());
	}
}


