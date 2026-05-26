package ru.itmo.saferoad.learning.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;
import ru.itmo.saferoad.learning.domain.UserQuestionStatsId;
import ru.itmo.saferoad.learning.domain.repository.UserQuestionStatsRepository;
import ru.itmo.saferoad.learning.dto.SubmitAnswerResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQuestionStatsServiceImplTest {

	@Mock
	private UserQuestionStatsRepository userQuestionStatsRepository;

	@Mock
	private ReviewScheduler reviewScheduler;

	@Mock
	private ru.itmo.saferoad.content.domain.repository.QuestionRepository questionRepository;

	@Mock
	private CurrentTime currentTime;

	@InjectMocks
	private UserQuestionStatsServiceImpl service;

	@Test
	void submitAnswer_whenCorrect_returnsCorrectResponseAndSaves() {
		Long userId = 1L;
		Long qId = 2L;

		QuestionContent.AnswerOption option = new QuestionContent.AnswerOption();
		option.setNumber(5);
		option.setIsCorrect(true);

		QuestionContent content = new QuestionContent();
		content.setOptions(List.of(option));

		Topic topic = new Topic();
		topic.setId(10);

		Question question = new Question();
		question.setId(qId);
		question.setTopic(topic);
		question.setContent(content);

		when(questionRepository.findById(qId)).thenReturn(Optional.of(question));
		when(userQuestionStatsRepository.findById(any(UserQuestionStatsId.class))).thenReturn(Optional.empty());
		when(reviewScheduler.getHoursForStreak(anyInt(), eq(true))).thenReturn(24);
		when(currentTime.nowDateTime()).thenReturn(LocalDateTime.of(2025, 1, 1, 0, 0));

		SubmitAnswerResponse res = service.submitAnswer(userId, qId, 5);

		assertTrue(res.isCorrect());
		assertEquals(10, res.getAwardedXp());
		verify(userQuestionStatsRepository, times(1)).save(any(UserQuestionStats.class));
	}

	@Test
	void submitAnswer_whenIncorrect_returnsIncorrectResponseAndSaves() {
		Long userId = 1L;
		Long qId = 3L;

		QuestionContent.AnswerOption option = new QuestionContent.AnswerOption();
		option.setNumber(2);
		option.setIsCorrect(false);

		QuestionContent content = new QuestionContent();
		content.setOptions(List.of(option));

		Topic topic = new Topic();
		topic.setId(11);

		Question question = new Question();
		question.setId(qId);
		question.setTopic(topic);
		question.setContent(content);

		when(questionRepository.findById(qId)).thenReturn(Optional.of(question));
		when(userQuestionStatsRepository.findById(any(UserQuestionStatsId.class))).thenReturn(Optional.empty());
		when(reviewScheduler.getHoursForStreak(anyInt(), eq(false))).thenReturn(0);
		when(currentTime.nowDateTime()).thenReturn(LocalDateTime.of(2025, 1, 2, 0, 0));

		SubmitAnswerResponse res = service.submitAnswer(userId, qId, 999);

		assertFalse(res.isCorrect());
		assertEquals(0, res.getAwardedXp());
		verify(userQuestionStatsRepository, times(1)).save(any(UserQuestionStats.class));
	}
}

