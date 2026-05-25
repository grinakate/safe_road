package ru.itmo.saferoad.auth.infrastructure.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itmo.saferoad.auth.api.dto.RegisterRequest;
import ru.itmo.saferoad.auth.api.dto.UserProfileResponse;
import ru.itmo.saferoad.auth.domain.UserRole;
import ru.itmo.saferoad.auth.domain.Users;
import ru.itmo.saferoad.core.time.CurrentTime;

/**
 * Маппер для данных пользователя.
 */
@Mapper(componentModel = "spring")
public abstract class UsersMapper {

	@Autowired
	protected CurrentTime currentTime;

	public abstract UserProfileResponse mapToProfileResponse(Users user);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "passwordHash", source = "passwordHash")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "lastLoginDate", ignore = true)
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "birthDate", expression = "java(source.getBirthDate() != null ? source.getBirthDate().atStartOfDay() : null)")
	public abstract Users mapToEntity(RegisterRequest source, String passwordHash);

	@AfterMapping
	public void fillRegistrationDefaults(@MappingTarget Users user) {
		java.time.LocalDateTime now = currentTime.nowDateTime();
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		user.setLastLoginDate(now);
		user.setRole(UserRole.USER);
	}
}
