package ru.itmo.saferoad.gamification.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.gamification.domain.XpHistory;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;
import ru.itmo.saferoad.gamification.domain.repository.XpHistoryRepository;

import java.time.LocalDate;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XpHistoryServiceImplTest {

	@Mock
	private XpHistoryRepository repository;

	@Mock
	private CurrentTime currentTime;

	@InjectMocks
	private XpHistoryServiceImpl service;

	@Test
	void getTop10ForCurrentWeek_shouldCallRepositoryWithWeekStart() {
		// Given
		LocalDate weekStart = LocalDate.of(2024, 12, 1);
		when(currentTime.weekStart()).thenReturn(weekStart);
		LeaderboardProjection p = org.mockito.Mockito.mock(LeaderboardProjection.class);
		when(repository.findWeekLeaderboardProjection(eq(weekStart), any())).thenReturn(List.of(p));

		// When
		var res = service.getTop10ForCurrentWeek();

		// Then
		assertEquals(1, res.size());
		assertEquals(p, res.get(0));
		verify(currentTime, times(1)).weekStart();
		verify(repository, times(1)).findWeekLeaderboardProjection(eq(weekStart), any());
	}

	@Test
	void getWeekXpByUser_shouldReturnXpOrZero() {
		// Given
		LocalDate weekStart = LocalDate.of(2025, 1, 1);
		when(currentTime.weekStart()).thenReturn(weekStart);
		when(repository.findByUserIdAndWeekStart(11L, weekStart)).thenReturn(java.util.Optional.of(Instancio.of(XpHistory.class)
				.set(field(XpHistory::getXp), 123L).create()));

		// When
		long xp = service.getWeekXpByUser(11L);

		// Then
		assertEquals(123L, xp);
		verify(currentTime, times(1)).weekStart();
	}

	@Test
	void getWeeklyRank_shouldDelegateToRepository() {
		// Given
		LocalDate weekStart = LocalDate.of(2025, 1, 8);
		when(currentTime.weekStart()).thenReturn(weekStart);
		when(repository.countByWeekStartAndXpGreaterThan(weekStart, 50L)).thenReturn(3L);

		// When
		long rank = service.getWeeklyRank(50L);

		// Then
		assertEquals(3L, rank);
		verify(currentTime, times(1)).weekStart();
		verify(repository, times(1)).countByWeekStartAndXpGreaterThan(weekStart, 50L);
	}

	@Test
	void getTop10ForCurrentWeek_whenNoData_shouldReturnEmptyList() {
		// Given
		LocalDate weekStart = LocalDate.of(2024, 12, 1);
		when(currentTime.weekStart()).thenReturn(weekStart);
		when(repository.findWeekLeaderboardProjection(eq(weekStart), any())).thenReturn(List.of());

		// When
		var res = service.getTop10ForCurrentWeek();

		// Then
		assertNotNull(res);
		assertTrue(res.isEmpty());
	}
}




