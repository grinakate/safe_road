package ru.itmo.saferoad.gamification.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.domain.UserAchievement;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAchievementServiceImplTest {

	@Mock
	private ru.itmo.saferoad.gamification.domain.repository.UserAchievementRepository userAchievementRepository;

	@Mock
	private ru.itmo.saferoad.gamification.domain.repository.AchievementRepository achievementRepository;

	@InjectMocks
	private UserAchievementServiceImpl service;

	@Test
	void getAchievementsByUserId_shouldReturnEmptyWhenNoAchievements() {
		// Given
		Long userId = 5L;
		when(userAchievementRepository.findByUserId(userId)).thenReturn(List.of());

		// When
		var res = service.getAchievementsByUserId(userId);

		// Then
		assertNotNull(res);
		assertTrue(res.isEmpty());
		verify(userAchievementRepository, times(1)).findByUserId(userId);
		verifyNoInteractions(achievementRepository);
	}

	@Test
	void getAchievementsByUserId_shouldReturnAchievementsForIds() {
		// Given
		Long userId = 7L;
		UserAchievement ua1 = Instancio.of(UserAchievement.class).set(field(UserAchievement::getAchievementId), 10).create();
		UserAchievement ua2 = Instancio.of(UserAchievement.class).set(field(UserAchievement::getAchievementId), 20).create();
		when(userAchievementRepository.findByUserId(userId)).thenReturn(List.of(ua1, ua2));

		Achievement a10 = Instancio.of(Achievement.class).set(field(Achievement::getId), 10).create();
		Achievement a20 = Instancio.of(Achievement.class).set(field(Achievement::getId), 20).create();
		when(achievementRepository.findAllById(List.of(10, 20))).thenReturn(List.of(a10, a20));

		// When
		var res = service.getAchievementsByUserId(userId);

		// Then
		assertEquals(2, res.size());
		verify(userAchievementRepository, times(1)).findByUserId(userId);
		verify(achievementRepository, times(1)).findAllById(List.of(10, 20));
	}
}

