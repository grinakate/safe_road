package ru.itmo.saferoad.content.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.content.domain.repository.TopicRepository;
import ru.itmo.saferoad.content.service.SectionService;

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
class TopicServiceImplTest {

	@Mock
	private TopicRepository topicRepository;

	@Mock
	private SectionService sectionService;

	@InjectMocks
	private TopicServiceImpl service;

	@Test
	void getBySectionId_shouldSortByOrderIndex() {
		// Given
		Integer sectionId = Instancio.create(Integer.class);
		Topic t1 = Instancio.of(Topic.class).set(field(Topic::getOrderIndex), 2).create();
		Topic t2 = Instancio.of(Topic.class).set(field(Topic::getOrderIndex), 1).create();
		when(topicRepository.findBySectionId(sectionId)).thenReturn(List.of(t1, t2));

		// When
		var res = service.getBySectionId(sectionId);

		// Then
		assertEquals(2, res.size());
		assertTrue(res.get(0).getOrderIndex() <= res.get(1).getOrderIndex());
		verify(topicRepository, times(1)).findBySectionId(sectionId);
	}

	@Test
	void create_shouldSetFieldsAndSave() {
		// Given
		Integer sectionId = Instancio.create(Integer.class);
		Section section = Instancio.of(Section.class).set(field(Section::getId), sectionId).create();
		when(sectionService.existingById(sectionId)).thenReturn(section);
		when(topicRepository.save(any(Topic.class))).thenAnswer(inv -> inv.getArgument(0));
		String name = Instancio.create(String.class);
		Integer order = Instancio.create(Integer.class);

		// When
		Topic topic = service.create(sectionId, name, "desc", order, 10);

		// Then
		assertNotNull(topic);
		assertEquals(section, topic.getSection());
		assertEquals(name, topic.getTitle());
		assertEquals(order, topic.getOrderIndex());
		assertTrue(topic.getIsActive());
		verify(topicRepository, times(1)).save(any(Topic.class));
	}

	@Test
	void create_whenSectionMissing_throws() {
		Integer sectionId = Instancio.create(Integer.class);
		when(sectionService.existingById(sectionId)).thenThrow(new IllegalArgumentException("nope"));

		assertThrows(IllegalArgumentException.class, () -> service.create(sectionId, "n", "d", 1, 5));
	}

	@Test
	void existingById_shouldReturnWhenFound() {
		// Given
		Integer id = Instancio.create(Integer.class);
		Topic t = Instancio.of(Topic.class).set(field(Topic::getId), id).create();
		when(topicRepository.findById(id)).thenReturn(Optional.of(t));

		// When
		Topic got = service.existingById(id);

		// Then
		assertSame(t, got);
		verify(topicRepository, times(1)).findById(id);
	}

	@Test
	void existingById_shouldThrowWhenMissing() {
		// Given
		Integer missing = Instancio.create(Integer.class);
		when(topicRepository.findById(missing)).thenReturn(Optional.empty());

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> service.existingById(missing));
	}

	@Test
	void getNextTopic_shouldReturnNextInSameSection() {
		// Given
		Integer secId = Instancio.create(Integer.class);
		Topic current = Instancio.of(Topic.class)
				.set(field(Topic::getOrderIndex), 1)
				.set(field(Topic::getSection), Instancio.of(Section.class).set(field(Section::getId), secId).create())
				.create();
		Topic next = Instancio.of(Topic.class).set(field(Topic::getOrderIndex), 2).create();
		when(topicRepository.findFirstBySectionIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(secId, current.getOrderIndex()))
				.thenReturn(Optional.of(next));

		// When
		var nextTopicOpt = service.getNextTopic(current);

		// Then
		assertTrue(nextTopicOpt.isPresent());
		assertSame(next, nextTopicOpt.get());
	}

	@Test
	void getNextTopic_shouldReturnNextFromNextSectionWhenNoNextInSameSection() {
		// Given
		Integer secId = Instancio.create(Integer.class);
		Topic current = Instancio.of(Topic.class)
				.set(field(Topic::getOrderIndex), 1)
				.set(field(Topic::getSection), Instancio.of(Section.class).set(field(Section::getId), secId).create())
				.create();
		when(topicRepository.findFirstBySectionIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(secId, current.getOrderIndex()))
				.thenReturn(Optional.empty());
		Section nextSection = Instancio.of(Section.class).set(field(Section::getId), 999).create();
		when(sectionService.getNextSection(current.getSection().getOrderIndex())).thenReturn(Optional.of(nextSection));
		Topic fromNextSection = Instancio.of(Topic.class).set(field(Topic::getOrderIndex), 1).create();
		when(topicRepository.findFirstBySectionIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(nextSection.getId(), 0))
				.thenReturn(Optional.of(fromNextSection));

		// When
		var nextFromNextSectionOpt = service.getNextTopic(current);

		// Then
		assertTrue(nextFromNextSectionOpt.isPresent());
		assertSame(fromNextSection, nextFromNextSectionOpt.get());
	}

	@Test
	void getNextTopic_shouldReturnEmptyWhenNoNextAnywhere() {
		// Given
		Integer secId = Instancio.create(Integer.class);
		Topic current = Instancio.of(Topic.class)
				.set(field(Topic::getOrderIndex), 1)
				.set(field(Topic::getSection), Instancio.of(Section.class).set(field(Section::getId), secId).create())
				.create();
		when(topicRepository.findFirstBySectionIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(secId, current.getOrderIndex()))
				.thenReturn(Optional.empty());
		when(sectionService.getNextSection(current.getSection().getOrderIndex())).thenReturn(Optional.empty());

		// When
		var emptyOpt = service.getNextTopic(current);

		// Then
		assertTrue(emptyOpt.isEmpty());
	}
}

