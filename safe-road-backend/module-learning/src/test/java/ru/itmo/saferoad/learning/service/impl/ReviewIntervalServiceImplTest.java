package ru.itmo.saferoad.learning.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.learning.domain.ReviewInterval;
import ru.itmo.saferoad.learning.domain.repository.ReviewIntervalRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewIntervalServiceImplTest {

	@Mock
	private ReviewIntervalRepository repository;

	@InjectMocks
	private ReviewIntervalServiceImpl service;

	@Test
	void existingByStreakLevel_whenExists_returnsInterval() {
		ReviewInterval iv = new ReviewInterval();
		iv.setStreakLevel(2);
		iv.setIntervalHours(12);
		when(repository.findById(2)).thenReturn(Optional.of(iv));

		ReviewInterval res = service.existingByStreakLevel(2);

		assertEquals(12, res.getIntervalHours());
	}

	@Test
	void existingByStreakLevel_whenMissing_throws() {
		when(repository.findById(99)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> service.existingByStreakLevel(99));
	}
}

