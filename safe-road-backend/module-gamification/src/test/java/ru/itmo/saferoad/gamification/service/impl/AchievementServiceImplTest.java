package ru.itmo.saferoad.gamification.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.gamification.api.dto.UserAchievementsResponse;
import ru.itmo.saferoad.gamification.domain.Achievement;
import ru.itmo.saferoad.gamification.service.UserAchievementService;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AchievementServiceImplTest {

	@Mock
	private ru.itmo.saferoad.gamification.domain.repository.AchievementRepository repository;

	@Mock
	private UserAchievementService userAchievementService;

	@InjectMocks
	private AchievementServiceImpl service;

	@Test
	void getAchievementsForUser_shouldIncludeActiveAndUnlocked() {
		// Given
		Achievement a1 = Instancio.of(Achievement.class)
				.set(field(Achievement::getId), 1)
				.set(field(Achievement::getIsActive), true)
				.create();
		Achievement a2 = Instancio.of(Achievement.class)
				.set(field(Achievement::getId), 2)
				.set(field(Achievement::getIsActive), false)
				.create();

		when(repository.findAll()).thenReturn(List.of(a1, a2));

		when(userAchievementService.getAchievementsByUserId(10L)).thenReturn(List.of(a2));

		// When
		List<UserAchievementsResponse> res = service.getAchievementsForUser(10L);

		// Then
		assertEquals(2, res.size());
		assertEquals(a1.getName(), res.get(0).getTitle());
		var unlockedEntry = res.stream().filter(r -> r.getTitle().equals(a2.getName())).findFirst().orElseThrow();
		assertTrue(unlockedEntry.isUnlocked());
		verify(repository, times(1)).findAll();
		verify(userAchievementService, times(1)).getAchievementsByUserId(10L);
	}

	@Test
	void getActiveAchievements_shouldReturnOnlyActiveFromRepository() {
		// Given
		Achievement a1 = Instancio.of(Achievement.class)
				.set(field(Achievement::getIsActive), true)
				.create();
		when(repository.findByIsActiveTrue()).thenReturn(List.of(a1));

		// When
		var res = service.getActiveAchievements();

		// Then
		assertEquals(1, res.size());
		assertTrue(res.get(0).getIsActive());
		verify(repository, times(1)).findByIsActiveTrue();
	}

	@Test
	void getAll_shouldReturnAllFromRepository() {
		// Given
		Achievement a1 = Instancio.of(Achievement.class).set(field(Achievement::getId), 1).create();
		Achievement a2 = Instancio.of(Achievement.class).set(field(Achievement::getId), 2).create();
		when(repository.findAll()).thenReturn(List.of(a1, a2));

		// When
		var res = service.getAll();

		// Then
		assertEquals(2, res.size());
		verify(repository, times(1)).findAll();
	}
}





