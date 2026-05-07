package ru.itmo.saferoad.auth.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public enum UserRole {

	ADMIN,
	USER,
	MODERATOR;

	/**
	 * Возвращает коллекцию {@link GrantedAuthority} для данной роли.
	 * Spring Security ожидает, что роли будут иметь префикс "ROLE_".
	 * Например, роль USER будет представлена как "ROLE_USER".
	 *
	 * @return Коллекция разрешений, связанных с этой ролью.
	 */
	public Collection<? extends GrantedAuthority> getAuthorities() {
		GrantedAuthority roleAuthority = new SimpleGrantedAuthority("ROLE_" + this.name());
		return List.of(roleAuthority);
	}
}
