package ru.itmo.saferoad.auth.infrastructure.mapper;

/**
 * Маппер для данных пользователя.
 */
//@Mapper(componentModel = "spring", uses = {LevelMapper.class})
public abstract class UsersMapper {
/*
	@Setter(onMethod_ = {@Autowired})
	protected PasswordEncoder passwordEncoder;

	@Setter(onMethod_ = {@Autowired})
	protected AvatarService avatarService;

	@Setter(onMethod_ = {@Autowired})
	protected LevelService levelService;

	@Setter(onMethod_ = {@Autowired})
	protected NewUserProperties newUserProperties;

	@Mapping(target = "avatarUrl", source = "user.avatar.url")
	public abstract UserRegisterResponse mapToRegisterResponse(Account user, String token);

	@Mapping(target = "avatarUrl", source = "user.avatar.url")
	public abstract UserResponse mapToResponse(Account user);

	public Account mapToEntity(@NonNull UserRegisterRequest source) {
		LocalDateTime now = LocalDateTime.now();

		Account user = new Account();
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
	}*/
}
