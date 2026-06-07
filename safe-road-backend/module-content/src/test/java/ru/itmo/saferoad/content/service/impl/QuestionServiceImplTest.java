package ru.itmo.saferoad.content.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;

import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

	@Mock
	private QuestionRepository repository;

	@InjectMocks
	private QuestionServiceImpl service;

	@Test
	void existingById_shouldReturnWhenFound() {
		Long id = 123L;
		Question q = Instancio.of(Question.class).set(field(Question::getId), id).create();
		when(repository.findById(id)).thenReturn(Optional.of(q));

		var res = service.existingById(id);
		assertSame(q, res);
	}

	@Test
	void existingById_shouldThrowWhenMissing() {
		Long id = 999L;
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> service.existingById(id));
	}

	@Test
	void save_shouldDelegateToRepository() {
		Question q = Instancio.create(Question.class);
		when(repository.save(q)).thenReturn(q);

		var res = service.save(q);
		assertEquals(q, res);
	}
}

