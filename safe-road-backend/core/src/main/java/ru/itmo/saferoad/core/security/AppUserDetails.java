package ru.itmo.saferoad.core.security;

import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

public interface AppUserDetails extends UserDetails {

	@NonNull
	Long getId();
}
