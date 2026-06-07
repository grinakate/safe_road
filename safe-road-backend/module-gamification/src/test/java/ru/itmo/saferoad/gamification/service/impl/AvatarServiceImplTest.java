package ru.itmo.saferoad.gamification.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.gamification.domain.Avatar;
import ru.itmo.saferoad.gamification.domain.repository.AvatarRepository;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

	@Test
	void existingById_shouldReturnWhenFound() {
		Integer id = 5;
		Avatar avatar = Instancio.of(Avatar.class)
				.set(field(Avatar::getId), id)
				.set(field(Avatar::getUrl), "/a.png")
				.set(field(Avatar::getMinLevel), 1)
				.create();

		when(repository.findById(id)).thenReturn(Optional.of(avatar));

		Avatar res = service.existingById(id);
		assertNotNull(res);
		assertEquals(id, res.getId());
		assertEquals("/a.png", res.getUrl());
	}

	@Test
	void findAll_shouldReturnRepositoryList() {
		Avatar a1 = Instancio.of(Avatar.class)
				.set(field(Avatar::getId), 1)
				.set(field(Avatar::getUrl), "/1.png")
				.set(field(Avatar::getMinLevel), 1)
				.create();
		Avatar a2 = Instancio.of(Avatar.class)
				.set(field(Avatar::getId), 2)
				.set(field(Avatar::getUrl), "/2.png")
				.set(field(Avatar::getMinLevel), 2)
				.create();

		when(repository.findAll()).thenReturn(List.of(a1, a2));

		var res = service.findAll();
		assertEquals(2, res.size());
		assertEquals(a1, res.get(0));
		assertEquals(a2, res.get(1));
	}
}

