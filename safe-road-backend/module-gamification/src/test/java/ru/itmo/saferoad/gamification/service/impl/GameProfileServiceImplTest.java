package ru.itmo.saferoad.gamification.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.gamification.domain.Avatar;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.Level;
import ru.itmo.saferoad.gamification.domain.repository.GameProfileRepository;
import ru.itmo.saferoad.gamification.service.AvatarService;
import ru.itmo.saferoad.gamification.service.LevelService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameProfileServiceImplTest {

	@Mock
	private LevelService levelService;

	@Mock
	private AvatarService avatarService;

	@Mock
	private GameProfileRepository repository;

	@InjectMocks
	private GameProfileServiceImpl service;

	@Test
	void create_shouldInitializeProfileAndSave() {
		// Given
		Long userId = 33L;
		Avatar avatar = Instancio.of(Avatar.class).create();
		Level level = Instancio.of(Level.class).create();
		when(avatarService.existingById(any())).thenReturn(avatar);
		when(levelService.existingByNumber(any())).thenReturn(level);
		when(repository.save(any(GameProfile.class))).thenAnswer(inv -> inv.getArgument(0));

		// When
		GameProfile res = service.create(userId, true);

		// Then
		assertNotNull(res);
		assertEquals(userId, res.getUserId());
		assertSame(avatar, res.getAvatar());
		assertSame(level, res.getLevel());
		assertEquals(0, res.getXp());
		assertTrue(res.getIsLeaderboardParticipant());
		verify(repository, times(1)).save(any(GameProfile.class));
	}

	@Test
	void setLeaderboardEnabled_shouldToggleFlagOnExistingProfile() {
		// Given
		Long userId = 99L;
		GameProfile profile = new GameProfile();
		profile.setUserId(userId);
		profile.setIsLeaderboardParticipant(true);
		when(repository.findById(userId)).thenReturn(java.util.Optional.of(profile));

		// When
		service.setLeaderboardEnabled(userId, false);

		// Then
		assertFalse(profile.getIsLeaderboardParticipant());
	}

	@Test
	void existingByUserId_shouldThrowWhenNotFound() {
		// Given
		Long userId = 9999L;
		when(repository.findById(userId)).thenReturn(java.util.Optional.empty());

		// When / Then
		assertThrows(RuntimeException.class, () -> service.existingByUserId(userId));
	}
}




