package ru.itmo.saferoad.learning.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.api.ContentService;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.content.dto.TopicBriefDto;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapServiceTest {

	@Mock
	private ContentService contentService;

	@Mock
	private UserTopicProgressService userTopicProgressService;

	private MapService mapService;

	@BeforeEach
	void setUp() {
		mapService = new MapService(contentService, userTopicProgressService);
	}

	@Test
	void getMapForUser_mapsStatusesAndCalculatesRoundedProgress() {
		SectionTreeDto section = SectionTreeDto.builder()
				.id(1)
				.title("Section")
				.description("desc")
				.url("/section")
				.orderIndex(1)
				.topics(List.of(
						TopicBriefDto.builder().id(101).title("T1").content("c1").orderIndex(1).build(),
						TopicBriefDto.builder().id(102).title("T2").content("c2").orderIndex(2).build(),
						TopicBriefDto.builder().id(103).title("T3").content("c3").orderIndex(3).build()
				))
				.build();

		UserTopicProgress p1 = new UserTopicProgress();
		p1.setUserId(42L);
		p1.setTopicId(101);
		p1.setStatus(ProgressStatus.COMPLETED);

		UserTopicProgress p2 = new UserTopicProgress();
		p2.setUserId(42L);
		p2.setTopicId(102);
		p2.setStatus(ProgressStatus.UNLOCKED);

		when(contentService.getSectionTree()).thenReturn(List.of(section));
		when(userTopicProgressService.getByUserId(42L)).thenReturn(List.of(p1, p2));

		List<UserSectionResponse> result = mapService.getMapForUser(42L);

		assertEquals(1, result.size());
		UserSectionResponse mappedSection = result.get(0);
		assertEquals(33, mappedSection.getProgressPercent());
		assertEquals(ProgressStatus.COMPLETED, mappedSection.getTopics().get(0).getStatus());
		assertEquals(ProgressStatus.UNLOCKED, mappedSection.getTopics().get(1).getStatus());
		assertEquals(ProgressStatus.LOCKED, mappedSection.getTopics().get(2).getStatus());
	}

	@Test
	void getMapForUser_returnsZeroProgressForEmptyTopics() {
		SectionTreeDto emptySection = SectionTreeDto.builder()
				.id(2)
				.title("Empty")
				.description("desc")
				.url("/empty")
				.orderIndex(2)
				.topics(List.of())
				.build();

		when(contentService.getSectionTree()).thenReturn(List.of(emptySection));
		when(userTopicProgressService.getByUserId(5L)).thenReturn(List.of());

		List<UserSectionResponse> result = mapService.getMapForUser(5L);

		assertEquals(1, result.size());
		assertEquals(0, result.get(0).getProgressPercent());
		assertTrue(result.get(0).getTopics().isEmpty());
	}

	@Test
	void getMapForUser_throwsWhenProgressContainsDuplicateTopicIds() {
		SectionTreeDto section = SectionTreeDto.builder()
				.id(3)
				.title("Section")
				.description("desc")
				.url("/section")
				.orderIndex(3)
				.topics(List.of(TopicBriefDto.builder().id(201).title("T").content("c").orderIndex(1).build()))
				.build();

		UserTopicProgress first = new UserTopicProgress();
		first.setUserId(7L);
		first.setTopicId(201);
		first.setStatus(ProgressStatus.UNLOCKED);

		UserTopicProgress duplicate = new UserTopicProgress();
		duplicate.setUserId(7L);
		duplicate.setTopicId(201);
		duplicate.setStatus(ProgressStatus.COMPLETED);

		when(contentService.getSectionTree()).thenReturn(List.of(section));
		when(userTopicProgressService.getByUserId(7L)).thenReturn(List.of(first, duplicate));

		assertThrows(IllegalStateException.class, () -> mapService.getMapForUser(7L));
	}
}


