package ru.itmo.saferoad.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.saferoad.auth.domain.UserRole;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

	private Long id;
	private String email;
	private String nickname;
	private LocalDateTime birthDate;
	private UserRole role;
    private LocalDateTime createdAt;
}

