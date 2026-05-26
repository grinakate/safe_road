package ru.itmo.saferoad.content.api;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.content.dto.CreateSectionRequest;
import ru.itmo.saferoad.content.dto.CreateTopicRequest;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.content.dto.TopicBriefDto;
import ru.itmo.saferoad.content.dto.UpdateQuestionRequest;
import ru.itmo.saferoad.content.dto.UpdateSectionRequest;
import ru.itmo.saferoad.content.dto.UpdateTopicRequest;
import ru.itmo.saferoad.content.service.QuestionService;
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.content.service.TopicService;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentControllerTest {

	@Mock
	private SectionService sectionService;

	@Mock
	private TopicService topicService;

	@Mock
	private QuestionService questionService;

	@Mock
	private ContentMapper contentMapper;

	@Mock
	private JsonMapper jsonMapper;

	@InjectMocks
	private ContentController controller;

	@Test
	void getTopicsForSection_shouldReturnMappedDtos() {
		// Given
		Integer sectionId = Instancio.create(Integer.class);
		TopicBriefDto dto = Instancio.create(TopicBriefDto.class);
		when(topicService.getBySectionId(sectionId)).thenReturn(List.of());
		when(contentMapper.toTopicBriefDtoList(List.of())).thenReturn(List.of(dto));

		// When
		ResponseEntity<List<TopicBriefDto>> resp = controller.getTopicsForSection(sectionId);

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertEquals(1, resp.getBody().size());
		assertSame(dto, resp.getBody().get(0));
		verify(topicService, times(1)).getBySectionId(sectionId);
	}

	@Test
	void getTopic_shouldReturnMappedDto() {
		// Given
		Integer id = Instancio.create(Integer.class);
		TopicBriefDto dto = Instancio.create(TopicBriefDto.class);
		when(topicService.existingById(id)).thenReturn(Instancio.create(ru.itmo.saferoad.content.domain.Topic.class));
		when(contentMapper.toTopicBriefDto(any())).thenReturn(dto);

		// When
		ResponseEntity<TopicBriefDto> resp = controller.getTopic(id);

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertSame(dto, resp.getBody());
		verify(topicService, times(1)).existingById(id);
	}

	@Test
	void getSectionTree_shouldReturnMappedList() {
		// Given
		var section = Instancio.create(ru.itmo.saferoad.content.domain.Section.class);
		SectionTreeDto dto = Instancio.create(SectionTreeDto.class);
		when(sectionService.getAllOrdered()).thenReturn(List.of(section));
		when(contentMapper.toSectionTreeDtoList(List.of(section))).thenReturn(List.of(dto));

		// When
		ResponseEntity<List<SectionTreeDto>> resp = controller.getSectionTree(null);

		// Then
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertEquals(1, resp.getBody().size());
		assertSame(dto, resp.getBody().get(0));
		verify(sectionService, times(1)).getAllOrdered();
	}

	@Test
	void createSection_shouldReturnCreatedDto() {
		// Given
		CreateSectionRequest req = Instancio.create(CreateSectionRequest.class);
		var section = Instancio.of(ru.itmo.saferoad.content.domain.Section.class)
				.set(field(ru.itmo.saferoad.content.domain.Section::getId), 5)
				.create();
		SectionTreeDto dto = Instancio.create(SectionTreeDto.class);
		when(sectionService.create(req.getName(), req.getOrderIndex())).thenReturn(section);
		when(contentMapper.toSectionTreeDto(section)).thenReturn(dto);

		// When
		ResponseEntity<SectionTreeDto> resp = controller.createSection(req, null);

		// Then
		assertEquals(HttpStatus.CREATED, resp.getStatusCode());
		assertSame(dto, resp.getBody());
		verify(sectionService, times(1)).create(req.getName(), req.getOrderIndex());
	}

	@Test
	void createTopic_shouldReturnCreatedDto() {
		// Given
		CreateTopicRequest req = Instancio.create(CreateTopicRequest.class);
		var topic = Instancio.of(ru.itmo.saferoad.content.domain.Topic.class)
				.set(field(ru.itmo.saferoad.content.domain.Topic::getId), 7)
				.create();
		TopicBriefDto dto = Instancio.create(TopicBriefDto.class);
		when(topicService.create(req.getSectionId(), req.getName(), req.getDescription(), req.getOrderIndex(), req.getXpReward())).thenReturn(topic);
		when(contentMapper.toTopicBriefDto(topic)).thenReturn(dto);

		// When
		ResponseEntity<TopicBriefDto> resp = controller.createTopic(req, null);

		// Then
		assertEquals(HttpStatus.CREATED, resp.getStatusCode());
		assertSame(dto, resp.getBody());
		verify(topicService, times(1)).create(req.getSectionId(), req.getName(), req.getDescription(), req.getOrderIndex(), req.getXpReward());
	}

	@Test
	void updateSection_shouldCallServiceAndReturnDto() {
		UpdateSectionRequest req = Instancio.create(UpdateSectionRequest.class);
		var section = Instancio.create(ru.itmo.saferoad.content.domain.Section.class);
		SectionTreeDto dto = Instancio.create(SectionTreeDto.class);
		when(sectionService.updateSection(5, req.getName(), req.getOrderIndex(), req.getIsActive())).thenReturn(section);
		when(contentMapper.toSectionTreeDto(section)).thenReturn(dto);

		ResponseEntity<?> resp = controller.updateSection(5, req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertSame(dto, resp.getBody());
		verify(sectionService, times(1)).updateSection(5, req.getName(), req.getOrderIndex(), req.getIsActive());
	}

	@Test
	void archiveSection_shouldCallService() {
		when(sectionService.updateSection(6, null, null, false)).thenReturn(Instancio.create(ru.itmo.saferoad.content.domain.Section.class));
		ResponseEntity<?> resp = controller.archiveSection(6);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		verify(sectionService, times(1)).updateSection(6, null, null, false);
	}

	@org.junit.jupiter.api.Test
	void updateTopic_shouldCallServiceAndReturnDto() {
		UpdateTopicRequest req = Instancio.create(UpdateTopicRequest.class);
		var topic = Instancio.create(ru.itmo.saferoad.content.domain.Topic.class);
		TopicBriefDto dto = Instancio.create(TopicBriefDto.class);
		when(topicService.updateTopic(7, req.getName(), req.getContent(), req.getOrderIndex(), req.getIsActive())).thenReturn(topic);
		when(contentMapper.toTopicBriefDto(topic)).thenReturn(dto);

		ResponseEntity<?> resp = controller.updateTopic(7, req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		assertSame(dto, resp.getBody());
		verify(topicService, times(1)).updateTopic(7, req.getName(), req.getContent(), req.getOrderIndex(), req.getIsActive());
	}

	@Test
	void updateQuestion_shouldParseJsonAndSave() {
		var q = Instancio.create(Question.class);
		when(questionService.existingById(300L)).thenReturn(q);

		UpdateQuestionRequest req = Instancio.create(UpdateQuestionRequest.class);
		QuestionContent content = Instancio.create(QuestionContent.class);
		when(jsonMapper.readValue(req.getContentJson(), QuestionContent.class)).thenReturn(content);

		ResponseEntity<?> resp = controller.updateQuestion(300L, req);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		verify(questionService, times(1)).save(q);
		assertNotNull(q.getContent());
		assertEquals(q.getContent().getQuestionText(), content.getQuestionText());
	}
}

