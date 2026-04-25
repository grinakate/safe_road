package ru.itmo.saferoad.app.mapper;

import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.saferoad.app.controller.dto.UserRegisterRequest;
import ru.itmo.saferoad.app.controller.dto.UserResponse;
import ru.itmo.saferoad.domain.entity.User;
import ru.itmo.saferoad.services.business.AvatarService;
import ru.itmo.saferoad.services.business.LevelService;

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

	/**
	 * Метод маппит сущность пользователя в ДТО.
	 *
	 * @param user сущность пользователя.
	 * @return ДТО.
	 */
	@Mapping(target = "avatarUrl", source = "user.avatar.imageUrl")
	public abstract UserResponse mapToResponse(User user);

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
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "role", expression = "java(ru.itmo.saferoad.domain.enums.UserRole.PARTICIPANT)")
	@Mapping(target = "password", expression = "java(passwordEncoder.encode(source.getPassword()))")
	@Mapping(target = "emailVerified", constant = "false")
	@Mapping(target = "avatar", expression = "java(avatarService.existingById(source.getAvatarId()))")
	@Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "currentXp", constant = "0L")
	@Mapping(target = "level", expression =
			"java(levelService.existingByNumber(ru.itmo.saferoad.domain.enums.LevelEnum.START_LEVEL.getNumber()))")
	@Mapping(target = "coins", constant = "0L")
	@Mapping(target = "authorities", ignore = true)
	public abstract User mapForCreateParticipant(UserRegisterRequest source);
}
