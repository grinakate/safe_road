package ru.itmo.saferoad.auth.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.auth.domain.repository.UsersRepository;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UsersRepository userRepository;

	@NonNull
	@Override
	public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		// Возвращаем объект MyUserDetails, который Spring Security будет использовать
		// для проверки пароля и авторизации.
		return new AppUserDetailsImpl(
				user.getId(),
				user.getEmail(),
				user.getPasswordHash(),
				user.getRole().getAuthorities()
		);
	}
}
