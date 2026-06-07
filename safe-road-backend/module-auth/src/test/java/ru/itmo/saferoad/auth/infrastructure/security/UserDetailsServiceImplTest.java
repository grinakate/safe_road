package ru.itmo.saferoad.auth.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.itmo.saferoad.auth.domain.UserRole;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.domain.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

	@Mock
	private UsersRepository userRepository;

	@InjectMocks
	private UserDetailsServiceImpl service;

	@Test
	void loadUserByUsername_whenUserExists_returnsDetails() {
		Users user = new Users();
		user.setId(7L);
		user.setEmail("a@b.com");
		user.setPasswordHash("secret");
		user.setNickname("nick");
		user.setRole(UserRole.USER);
		user.setBirthDate(LocalDateTime.now());
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());
		user.setLastLoginDate(LocalDateTime.now());

		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

		var details = service.loadUserByUsername("a@b.com");

		assertNotNull(details);
		assertEquals("a@b.com", details.getUsername());
		assertEquals("secret", details.getPassword());
	}

	@Test
	void loadUserByUsername_whenMissing_throws() {
		when(userRepository.findByEmail("none@x.com")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("none@x.com"));
	}
}

