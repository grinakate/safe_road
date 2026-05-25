package ru.itmo.saferoad.gamification.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.gamification.domain.repository.AvatarRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarServiceImplTest {

	@Mock
	private AvatarRepository repository;

	@InjectMocks
	private AvatarServiceImpl service;

	@Test
	void existingById_shouldThrowWhenMissing() {
		// Given
		Integer id = 123;
		when(repository.findById(id)).thenReturn(java.util.Optional.empty());

		// When / Then
		assertThrows(IllegalArgumentException.class, () -> service.existingById(id));
	}
}

