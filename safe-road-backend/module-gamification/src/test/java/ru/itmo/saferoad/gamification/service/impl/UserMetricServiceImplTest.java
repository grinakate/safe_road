package ru.itmo.saferoad.gamification.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.gamification.domain.UserMetric;
import ru.itmo.saferoad.gamification.domain.XpHistory;
import ru.itmo.saferoad.gamification.domain.repository.UserMetricRepository;
import ru.itmo.saferoad.gamification.domain.repository.XpHistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMetricServiceImplTest {

	@Mock
	private UserMetricRepository userMetricRepository;

	@Mock
	private XpHistoryRepository xpHistoryRepository;

	@Mock
	private CurrentTime currentTime;

	@InjectMocks
	private UserMetricServiceImpl service;

	@Test
	void addXp_shouldCreateXpHistoryWhenMissingAndSetUpdatedAt() {
		// Given
		Long userId = 55L;
		when(userMetricRepository.findByUserIdAndMetricCode(userId, "xp")).thenReturn(Optional.empty());
		when(currentTime.weekStart()).thenReturn(LocalDate.of(2024, 12, 9));
		LocalDateTime now = LocalDateTime.of(2024, 12, 10, 12, 0);
		when(currentTime.nowDateTime()).thenReturn(now);
		when(xpHistoryRepository.findByUserIdAndWeekStart(userId, LocalDate.of(2024, 12, 9))).thenReturn(Optional.empty());

		// When
		service.addXp(userId, 100);

		// Then
		ArgumentCaptor<XpHistory> captor = ArgumentCaptor.forClass(XpHistory.class);
		verify(xpHistoryRepository, times(1)).save(captor.capture());
		XpHistory saved = captor.getValue();
		assertEquals(userId, saved.getUserId());
		assertEquals(100L, saved.getXp());
		assertEquals(now, saved.getUpdatedAt());
		verify(userMetricRepository, times(1)).save(any(UserMetric.class));
	}

	@Test
	void getXp_shouldReturnValueWhenPresent() {
		// Given
		Long userId = 10L;
		UserMetric metric = new UserMetric();
		metric.setUserId(userId);
		metric.setMetricCode("xp");
		metric.setValue(500L);
		when(userMetricRepository.findByUserIdAndMetricCode(userId, "xp")).thenReturn(Optional.of(metric));

		// When
		long xp = service.getXp(userId);

		// Then
		assertEquals(500L, xp);
	}

	@Test
	void getXp_shouldReturnZeroWhenMissing() {
		// Given
		Long userId = 11L;
		when(userMetricRepository.findByUserIdAndMetricCode(userId, "xp")).thenReturn(Optional.empty());

		// When
		long xp = service.getXp(userId);

		// Then
		assertEquals(0L, xp);
	}

	@Test
	void getRank_shouldUseRepositoryCount() {
		// Given
		Long userId = 12L;
		when(userMetricRepository.findByUserIdAndMetricCode(userId, "xp")).thenReturn(Optional.of(new UserMetric() {
			{
				setValue(100L);
			}
		}));
		when(userMetricRepository.countByMetricCodeAndValueGreaterThan("xp", 100L)).thenReturn(4L);

		// When
		long rank = service.getRank(userId);

		// Then
		assertEquals(5L, rank);
	}

	@Test
	void getLeaderboardTop10ByXp_shouldDelegateToRepository() {
		// Given
		when(userMetricRepository.findTop10ByMetricCodeOrderByValueDesc("xp")).thenReturn(List.of());

		// When
		var res = service.getLeaderboardTop10ByXp();

		// Then
		assertNotNull(res);
		verify(userMetricRepository, times(1)).findTop10ByMetricCodeOrderByValueDesc("xp");
	}

	@Test
	void addXp_shouldUpdateExistingXpHistory() {
		// Given
		Long userId = 56L;
		when(userMetricRepository.findByUserIdAndMetricCode(userId, "xp")).thenReturn(Optional.empty());
		LocalDate weekStart = LocalDate.of(2024, 12, 9);
		when(currentTime.weekStart()).thenReturn(weekStart);
		LocalDateTime now = LocalDateTime.of(2024, 12, 10, 12, 0);
		when(currentTime.nowDateTime()).thenReturn(now);

		XpHistory existing = new XpHistory();
		existing.setUserId(userId);
		existing.setWeekStart(weekStart);
		existing.setXp(10L);
		when(xpHistoryRepository.findByUserIdAndWeekStart(userId, weekStart)).thenReturn(Optional.of(existing));

		// When
		service.addXp(userId, 5);

		// Then
		verify(xpHistoryRepository, times(1)).save(existing);
		assertEquals(15L, existing.getXp());
		assertEquals(now, existing.getUpdatedAt());
	}

	@Test
	void addXp_withNegativeDelta_shouldNotModifyXpHistory() {
		// Given
		Long userId = 57L;
		
		// When
		service.addXp(userId, -5);

		// Then
		verify(xpHistoryRepository, never()).save(any());
	}
}
