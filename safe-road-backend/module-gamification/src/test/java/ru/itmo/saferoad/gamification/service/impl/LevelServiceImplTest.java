package ru.itmo.saferoad.gamification.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.gamification.domain.Level;
import ru.itmo.saferoad.gamification.domain.repository.LevelRepository;

import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LevelServiceImplTest {

	@Mock
	private LevelRepository repository;

	@InjectMocks
	private LevelServiceImpl service;

	@Test
	void getXpProgress_shouldReturnOneWhenPreviousLevelMissing() {
		// Given
		Integer levelNumber = 2;
		Level current = Instancio.of(Level.class)
				.set(field(Level::getNumber), levelNumber)
				.set(field(Level::getXpThreshold), 100)
				.create();
		when(repository.findByNumber(levelNumber)).thenReturn(Optional.of(current));
		when(repository.findByNumber(levelNumber - 1)).thenReturn(Optional.empty());

		// When
		Double progress = service.getXpProgress(levelNumber, 50);

		// Then
		assertEquals(1.0, progress);
		verify(repository, times(1)).findByNumber(levelNumber);
	}

	@Test
	void getXpProgress_shouldComputeFractionBetweenPreviousAndCurrent() {
		// Given
		Integer levelNumber = 3;
		Level prev = Instancio.of(Level.class)
				.set(field(Level::getNumber), levelNumber - 1)
				.set(field(Level::getXpThreshold), 100)
				.create();
		Level current = Instancio.of(Level.class)
				.set(field(Level::getNumber), levelNumber)
				.set(field(Level::getXpThreshold), 200)
				.create();
		when(repository.findByNumber(levelNumber)).thenReturn(Optional.of(current));
		when(repository.findByNumber(levelNumber - 1)).thenReturn(Optional.of(prev));

		// When
		Double progress = service.getXpProgress(levelNumber, 150);

		// Then
		assertEquals(0.5, progress);
	}

	@Test
	void getXpProgress_atBoundaries_shouldReturnZeroOrOne() {
		// Given
		Integer levelNumber = 3;
		Level prev = Instancio.of(Level.class)
				.set(field(Level::getNumber), levelNumber - 1)
				.set(field(Level::getXpThreshold), 100)
				.create();
		Level current = Instancio.of(Level.class)
				.set(field(Level::getNumber), levelNumber)
				.set(field(Level::getXpThreshold), 200)
				.create();
		when(repository.findByNumber(levelNumber)).thenReturn(Optional.of(current));
		when(repository.findByNumber(levelNumber - 1)).thenReturn(Optional.of(prev));

		// When/Then: at previous threshold -> 0
		assertEquals(0.0, service.getXpProgress(levelNumber, 100));
		// at current threshold -> 1
		assertEquals(1.0, service.getXpProgress(levelNumber, 200));
	}

	@Test
	void existingByNumber_shouldThrowWhenNotFound() {
		// Given
		when(repository.findByNumber(999)).thenReturn(Optional.empty());

		// When / Then
		assertThrows(RuntimeException.class, () -> service.existingByNumber(999));
	}
}



