package ru.itmo.saferoad.content.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.repository.SectionRepository;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionServiceImplTest {

	@Mock
	private SectionRepository repository;

	@InjectMocks
	private SectionServiceImpl service;

	@Test
	void getAllOrdered_shouldSortByOrderIndex() {
		// Given
		Section s1 = Instancio.of(Section.class).set(field(Section::getOrderIndex), 2).create();
		Section s2 = Instancio.of(Section.class).set(field(Section::getOrderIndex), 1).create();
		Section s3 = Instancio.of(Section.class).set(field(Section::getOrderIndex), 3).create();
		when(repository.findAll()).thenReturn(List.of(s1, s2, s3));

		// When
		var result = service.getAllOrdered();

		// Then
		assertEquals(3, result.size());
		assertTrue(result.get(0).getOrderIndex() <= result.get(1).getOrderIndex());
		assertTrue(result.get(1).getOrderIndex() <= result.get(2).getOrderIndex());
		verify(repository, times(1)).findAll();
	}

	@Test
	void create_shouldSetFieldsAndSave() {
		// Given
		String name = Instancio.create(String.class);
		Integer order = Instancio.create(Integer.class);
		when(repository.save(any(Section.class))).thenAnswer(inv -> inv.getArgument(0));

		// When
		Section created = service.create(name, order);

		// Then
		assertNotNull(created);
		assertEquals(name, created.getTitle());
		assertEquals(order, created.getOrderIndex());
		verify(repository, times(1)).save(any(Section.class));
	}

	@Test
	void existingById_shouldReturnWhenFoundAndThrowWhenMissing() {
		// Given
		Integer id = Instancio.create(Integer.class);
		Section s = Instancio.of(Section.class).set(field(Section::getId), id).create();
		when(repository.findById(id)).thenReturn(Optional.of(s));

		// When
		Section found = service.existingById(id);

		// Then
		assertSame(s, found);
		verify(repository, times(1)).findById(id);
	}

	@Test
	void existingById_shouldThrowWhenMissing() {
		// Given
		Integer missingId = Instancio.create(Integer.class);
		when(repository.findById(missingId)).thenReturn(Optional.empty());

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> service.existingById(missingId));
		verify(repository, times(1)).findById(missingId);
	}

	@Test
	void getFirstSection_and_getNextSection_behaviour() {
		// Given
		Section first = Instancio.of(Section.class).set(field(Section::getOrderIndex), 1).create();
		when(repository.findFirstByOrderByOrderIndexAsc()).thenReturn(Optional.of(first));

		// When
		Section got = service.getFirstSection();

		// Then
		assertSame(first, got);
		verify(repository, times(1)).findFirstByOrderByOrderIndexAsc();
	}

	@Test
	void getNextSection_shouldReturnNextWhenPresent() {
		// Given
		Integer current = 2;
		Section next = Instancio.of(Section.class).set(field(Section::getOrderIndex), 3).create();
		when(repository.findFirstByOrderIndexGreaterThanOrderByOrderIndexAsc(current)).thenReturn(Optional.of(next));

		// When
		var nextOpt = service.getNextSection(current);

		// Then
		assertTrue(nextOpt.isPresent());
		assertSame(next, nextOpt.get());
		verify(repository, times(1)).findFirstByOrderIndexGreaterThanOrderByOrderIndexAsc(current);
	}
}

