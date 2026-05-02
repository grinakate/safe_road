package ru.itmo.saferoad.profile.mapper;

import lombok.NonNull;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.saferoad.profile.dto.user.UserRegisterRequest;
import ru.itmo.saferoad.profile.dto.user.UserRegisterResponse;
import ru.itmo.saferoad.profile.dto.user.UserResponse;
import ru.itmo.saferoad.profile.config.NewUserProperties;
import ru.itmo.saferoad.profile.domain.User;
import ru.itmo.saferoad.profile.domain.UserRole;
import ru.itmo.saferoad.profile.service.AvatarService;
import ru.itmo.saferoad.profile.service.LevelService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
		LocalDateTime now = LocalDateTime.now();

		User user = new User();
		user.setEmail(source.getEmail());
		user.setName(source.getName());
		user.setBirthDate(source.getBirthDate().atStartOfDay());
		user.setRole(UserRole.USER);
		user.setPasswordHash(Objects.requireNonNull(passwordEncoder.encode(source.getPassword())));
		user.setAvatar(avatarService.existingById(source.getAvatarId()));
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		user.setLastLoginDate(now);
		user.setCurrentXp(newUserProperties.getStartXp());
		user.setCurrentStreak(newUserProperties.getStartStreak());
		user.setLevel(levelService.existingByNumber(newUserProperties.getStartLevelNumber()));

		boolean isLeaderboardVisible =
				ChronoUnit.YEARS.between(now, source.getBirthDate().atStartOfDay()) >= newUserProperties.getMinAgeForLeaderboard();
		user.setIsLeaderboardVisible(isLeaderboardVisible);

		return user;
	}
}
