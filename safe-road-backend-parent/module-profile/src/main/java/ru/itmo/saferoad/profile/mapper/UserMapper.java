package ru.itmo.saferoad.profile.mapper;

import lombok.NonNull;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.saferoad.core.dto.profile.user.UserRegisterRequest;
import ru.itmo.saferoad.core.dto.profile.user.UserRegisterResponse;
import ru.itmo.saferoad.core.dto.profile.user.UserResponse;
import ru.itmo.saferoad.profile.config.NewUserProperties;
import ru.itmo.saferoad.profile.domain.User;
import ru.itmo.saferoad.profile.service.AvatarService;
import ru.itmo.saferoad.profile.service.LevelService;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Маппер для данных пользователя.
 */
@Mapper(componentModel = "spring", uses = {LevelMapper.class})
public abstract class UserMapper {

	@Setter(onMethod_ = {@Autowired})
	protected PasswordEncoder passwordEncoder;

	@Setter(onMethod_ = {@Autowired})
	protected AvatarService avatarService;

	@Setter(onMethod_ = {@Autowired})
	protected LevelService levelService;

	@Setter(onMethod_ = {@Autowired})
	protected NewUserProperties newUserProperties;

	@Mapping(target = "avatarUrl", source = "user.avatar.url")
	public abstract UserRegisterResponse mapToRegisterResponse(User user, String token);

	@Mapping(target = "avatarUrl", source = "user.avatar.url")
	public abstract UserResponse mapToResponse(User user);

	public User mapToEntity(@NonNull UserRegisterRequest source) {
		User user = new User();
		user.setEmail(source.getEmail());
		user.setName(source.getName());
		user.setBirthDate(source.getBirthDate().atStartOfDay());
		user.setRole(ru.itmo.saferoad.profile.domain.UserRole.USER);
		user.setPasswordHash(Objects.requireNonNull(passwordEncoder.encode(source.getPassword())));
		user.setAvatar(avatarService.existingById(source.getAvatarId()));
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());
		user.setLastLoginDate(LocalDateTime.now());
		user.setCurrentXp(newUserProperties.getStartXp());
		user.setCurrentStreak(newUserProperties.getStartStreak());
		user.setLevel(levelService.existingByNumber(newUserProperties.getStartLevelNumber()));

		return user;
	}
}
