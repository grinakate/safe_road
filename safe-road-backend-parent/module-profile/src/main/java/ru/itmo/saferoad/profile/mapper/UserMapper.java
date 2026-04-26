package ru.itmo.saferoad.profile.mapper;

import lombok.NonNull;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.saferoad.core.dto.UserRegisterRequest;
import ru.itmo.saferoad.core.dto.UserResponse;
import ru.itmo.saferoad.profile.config.NewUserProperties;
import ru.itmo.saferoad.profile.domain.User;
import ru.itmo.saferoad.profile.service.AvatarService;
import ru.itmo.saferoad.profile.service.LevelService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Маппер для данных пользователя.
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper {

	@Setter(onMethod_ = {@Autowired})
	protected PasswordEncoder passwordEncoder;

	@Setter(onMethod_ = {@Autowired})
	protected AvatarService avatarService;

	@Setter(onMethod_ = {@Autowired})
	protected LevelService levelService;

	@Setter(onMethod_ = {@Autowired})
	protected NewUserProperties newUserProperties;

	/**
	 * Метод маппит сущность пользователя в ДТО.
	 *
	 * @param user сущность пользователя.
	 * @return ДТО.
	 */
	@Mapping(target = "avatarUrl", source = "user.avatar.url")
	public abstract UserResponse mapToResponse(User user, String token);

	/**
	 * Метод маппит список сущностей пользователей в ДТО.
	 *
	 * @param users список сущностей пользователя.
	 * @return список ДТО.
	 */
	public abstract List<UserResponse> mapToResponse(List<User> users);

	/**
	 * Метод маппит данные из запроса в сущность для сохранения в БД.
	 *
	 * @param source данные пользователя из запроса.
	 * @return сущность пользователя.
	 */
	public User mapForCreateUser(@NonNull UserRegisterRequest source) {
		User user = new User();
		user.setEmail(source.getEmail());
		user.setName(source.getName());
		user.setBirthDate(source.getBirthDate().atStartOfDay());
		user.setRole(ru.itmo.saferoad.profile.domain.UserRole.USER);
		user.setPasswordHash(passwordEncoder.encode(source.getPassword()));
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
