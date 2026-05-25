package ru.itmo.saferoad.content.api;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.content.dto.TopicBriefDto;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ContentMapperTest {

	private final ContentMapper mapper = new ContentMapperImpl();

	@Test
	void toTopicBriefDto_and_list_shouldMapFields() {
		// Given
		Topic t = Instancio.of(Topic.class)
				.set(field(Topic::getId), 10)
				.set(field(Topic::getTitle), "T")
				.set(field(Topic::getContent), "C")
				.set(field(Topic::getOrderIndex), 5)
				.create();

		// When
		TopicBriefDto dto = mapper.toTopicBriefDto(t);
		List<TopicBriefDto> list = mapper.toTopicBriefDtoList(List.of(t));

		// Then
		assertNotNull(dto);
		assertEquals(t.getId(), dto.getId());
		assertEquals(t.getTitle(), dto.getTitle());
		assertEquals(t.getContent(), dto.getContent());
		assertEquals(t.getOrderIndex(), dto.getOrderIndex());
		assertEquals(1, list.size());
	}

	@Test
	void toSectionTreeDto_and_list_shouldMapFieldsAndTopics() {
		// Given
		Topic t = Instancio.of(Topic.class)
				.set(field(Topic::getId), 11)
				.set(field(Topic::getTitle), "TT")
				.set(field(Topic::getContent), "CC")
				.set(field(Topic::getOrderIndex), 1)
				.create();
		Section s = Instancio.of(Section.class)
				.set(field(Section::getId), 2)
				.set(field(Section::getTitle), "S")
				.set(field(Section::getDescription), "D")
				.set(field(Section::getUrl), "U")
				.set(field(Section::getOrderIndex), 7)
				.set(field(Section::getTopics), List.of(t))
				.create();

		// When
		SectionTreeDto dto = mapper.toSectionTreeDto(s);
		var list = mapper.toSectionTreeDtoList(List.of(s));

		// Then
		assertNotNull(dto);
		assertEquals(s.getId(), dto.getId());
		assertEquals(s.getTitle(), dto.getTitle());
		assertEquals(s.getDescription(), dto.getDescription());
		assertEquals(s.getUrl(), dto.getUrl());
		assertEquals(s.getOrderIndex(), dto.getOrderIndex());
		assertNotNull(dto.getTopics());
		assertEquals(1, dto.getTopics().size());
		assertEquals(1, list.size());
	}
}

