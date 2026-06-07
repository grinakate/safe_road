package ru.itmo.saferoad.learning.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.api.ContentService;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.learning.api.SectionMapper;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;
import ru.itmo.saferoad.learning.domain.repository.UserQuestionStatsRepository;
import ru.itmo.saferoad.learning.dto.TopicUserMapResponse;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

	@Mock
	private ContentService contentService;

	@Mock
	private UserTopicProgressService userTopicProgressService;

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private UserQuestionStatsRepository userQuestionStatsRepository;

	@Mock
	private SectionMapper sectionMapper;

	@InjectMocks
	private StatisticsService statisticsService;

	@Test
	void computeTopicStat_countsAreCorrect() {
		// Given
		Long userId = Instancio.create(Long.class);
		TopicUserMapResponse topic = Instancio.of(TopicUserMapResponse.class)
				.create();

		Question q1 = Instancio.of(Question.class)
				.create();
		q1.setContent(Instancio.create(QuestionContent.class));
		Question q2 = Instancio.of(Question.class)
				.create();
		q2.setContent(Instancio.create(QuestionContent.class));

		when(questionRepository.findByTopicId(topic.getId())).thenReturn(List.of(q1, q2));

		UserQuestionStats s1 = Instancio.of(UserQuestionStats.class)
				.set(field(UserQuestionStats::getQuestionId), q1.getId())
				.set(field(UserQuestionStats::getLastResultCorrect), true)
				.create();
		UserQuestionStats s2 = Instancio.of(UserQuestionStats.class)
				.set(field(UserQuestionStats::getQuestionId), q2.getId())
				.set(field(UserQuestionStats::getLastResultCorrect), false)
				.create();

		when(userQuestionStatsRepository.findByUserIdAndTopicId(userId, topic.getId())).thenReturn(List.of(s1, s2));

		// When
		var dto = statisticsService.computeTopicStat(topic, userId);

		// Then
		assertNotNull(dto);
		assertEquals(2, dto.getTotalQuestions());
		assertEquals(1, dto.getCorrectAnswers());
		assertEquals(1, dto.getWrongAnswers());
		assertEquals(0, dto.getNotShown());
	}

	@Test
	void buildSectionStats_aggregatesTopicStatsAndCompleted() {
		// Given
		Long userId = Instancio.create(Long.class);
		TopicUserMapResponse t1 = Instancio.of(TopicUserMapResponse.class)
				.create();
		TopicUserMapResponse t2 = Instancio.of(TopicUserMapResponse.class)
				.create();

		UserSectionResponse section = Instancio.of(UserSectionResponse.class)
				.set(field(UserSectionResponse::getId), 5)
				.set(field(UserSectionResponse::getTopics), List.of(t1, t2))
				.create();

		Question q1 = Instancio.of(Question.class).create();
		q1.setContent(Instancio.create(QuestionContent.class));
		when(questionRepository.findByTopicId(t1.getId())).thenReturn(List.of(q1));
		when(userQuestionStatsRepository.findByUserIdAndTopicId(userId, t1.getId())).thenReturn(List.of());

		Question q2 = Instancio.of(Question.class).create();
		q2.setContent(Instancio.create(QuestionContent.class));
		when(questionRepository.findByTopicId(t2.getId())).thenReturn(List.of(q2));
		UserQuestionStats s = Instancio.of(UserQuestionStats.class)
				.set(field(UserQuestionStats::getLastResultCorrect), true)
				.create();
		when(userQuestionStatsRepository.findByUserIdAndTopicId(userId, t2.getId())).thenReturn(List.of(s));

		var completedTopicsCount = 456;
		when(userTopicProgressService.countCompletedTopicsInSection(userId, section.getId()))
				.thenReturn(completedTopicsCount);

		// When
		var sectionDto = statisticsService.buildSectionStats(section, userId);

		// Then
		assertNotNull(sectionDto);
		assertEquals(section.getId(), sectionDto.getSectionId());
		assertEquals(2, sectionDto.getTopicStatistics().size());
		assertEquals(completedTopicsCount, sectionDto.getCompletedTopics());
	}

	@Test
	void computeTopicStat_whenNoQuestions_returnsZeros() {
		Long userId = Instancio.create(Long.class);
		TopicUserMapResponse topic = Instancio.of(TopicUserMapResponse.class).create();

		when(questionRepository.findByTopicId(topic.getId())).thenReturn(List.of());

		var dto = statisticsService.computeTopicStat(topic, userId);

		assertNotNull(dto);
		assertEquals(0, dto.getTotalQuestions());
		assertEquals(0, dto.getCorrectAnswers());
		assertEquals(0, dto.getWrongAnswers());
		assertEquals(0, dto.getNotShown());
	}
}



