package ru.itmo.saferoad.auth.infrastructure.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.itmo.saferoad.auth.api.dto.RegisterRequest;
import ru.itmo.saferoad.auth.api.dto.UserProfileResponse;
import ru.itmo.saferoad.auth.domain.UserRole;
import ru.itmo.saferoad.auth.domain.Users;

import java.time.LocalDateTime;

/**
 * Маппер для данных пользователя.
 */
@Mapper(componentModel = "spring")
public interface UsersMapper {

	UserProfileResponse mapToProfileResponse(Users user);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", source = "passwordHash")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "lastLoginDate", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "birthDate", expression = "java(source.getBirthDate() != null ? source.getBirthDate().atStartOfDay() : null)")
	Users mapToEntity(RegisterRequest source, String passwordHash);

	@AfterMapping
	default void fillRegistrationDefaults(@MappingTarget Users user, RegisterRequest source) {
		LocalDateTime now = LocalDateTime.now();
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		user.setLastLoginDate(now);
		user.setRole(UserRole.USER);
	}
}
