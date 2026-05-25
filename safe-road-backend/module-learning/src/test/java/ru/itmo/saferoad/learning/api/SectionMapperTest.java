package ru.itmo.saferoad.learning.api;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.content.dto.TopicBriefDto;
import ru.itmo.saferoad.learning.dto.TopicUserMapResponse;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SectionMapperTest {

	private final SectionMapper mapper = new SectionMapperImpl();

	@Test
	void toUserSectionResponse_mapsFieldsAndTopics() {
		TopicBriefDto topic = Instancio.create(TopicBriefDto.class);
		SectionTreeDto section = Instancio.of(SectionTreeDto.class)
				.set(field(SectionTreeDto::getTopics), List.of(topic))
				.create();

		// When
		UserSectionResponse dto = mapper.toUserSectionResponse(section);

		// Then
		assertNotNull(dto);
		assertEquals(section.getId(), dto.getId());
		assertEquals(section.getTitle(), dto.getTitle());
		assertEquals(section.getDescription(), dto.getDescription());
		assertEquals(section.getUrl(), dto.getUrl());
		assertEquals(0, dto.getProgressPercent());
		assertNotNull(dto.getTopics());
		assertEquals(1, dto.getTopics().size());
		TopicUserMapResponse t = dto.getTopics().get(0);
		assertEquals(topic.getId(), t.getId());
		assertEquals(topic.getTitle(), t.getTitle());
		assertEquals(topic.getOrderIndex(), t.getOrderIndex());
		assertEquals(ru.itmo.saferoad.core.domain.enums.ProgressStatus.LOCKED, t.getStatus());
	}

	@Test
	void toTopicUserMapResponse_mapsSingleTopic() {
		// Given
		TopicBriefDto topic = Instancio.create(TopicBriefDto.class);

		// When
		TopicUserMapResponse dto = mapper.toTopicUserMapResponse(topic);

		// Then
		assertNotNull(dto);
		assertEquals(topic.getId(), dto.getId());
		assertEquals(topic.getTitle(), dto.getTitle());
		assertEquals(topic.getOrderIndex(), dto.getOrderIndex());
		assertEquals(ru.itmo.saferoad.core.domain.enums.ProgressStatus.LOCKED, dto.getStatus());
	}

	@Test
	void toUserSectionResponseList_mapsListOfSections() {
		// Given
		SectionTreeDto s1 = Instancio.create(SectionTreeDto.class);
		SectionTreeDto s2 = Instancio.create(SectionTreeDto.class);

		// When
		var list = mapper.toUserSectionResponseList(List.of(s1, s2));

		// Then
		assertNotNull(list);
		assertEquals(2, list.size());
		assertEquals(s1.getId(), list.get(0).getId());
		assertEquals(s2.getId(), list.get(1).getId());
	}
}




