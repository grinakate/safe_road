package ru.itmo.saferoad.profile.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.itmo.saferoad.core.security.AppUserDetails;

import java.util.Collection;

/**
 * Custom реализация интерфейса {@link UserDetails} Spring Security.
 * Этот класс инкапсулирует информацию о пользователе, необходимую Spring Security
 * для аутентификации и авторизации.
 * Дополнительно содержит ID пользователя для удобства использования в бизнес-логике и выражениях SpEL.
 */
@NoArgsConstructor
@AllArgsConstructor
public class AppUserDetailsImpl implements AppUserDetails {

	@Getter
	@NonNull
	private Long id;

	@NonNull
	private String email;

	@NonNull
	private String password;

	@NonNull
	private Collection<? extends GrantedAuthority> authorities;

	@NonNull
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@NonNull
	@Override
	public String getPassword() {
		return password;
	}

	@NonNull
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
