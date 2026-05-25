package ru.itmo.saferoad.learning.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.learning.config.LearningProperties;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;

import java.time.LocalDateTime;
import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionSelectorTest {

	@Mock
	private CurrentTime currentTime;

	@Test
	void selectQuestions_adaptiveDistribution_selectsExpectedCounts() {
		// Given
		LearningProperties props = new LearningProperties();
		props.setReviewRatio(0.6);
		props.setNewRatio(0.3);

		LocalDateTime now = LocalDateTime.of(2026, 5, 25, 12, 0);
		when(currentTime.nowDateTime()).thenReturn(now);

		Question q1 = Instancio.of(Question.class).set(field(Question::getId), 1L).create();
		Question q2 = Instancio.of(Question.class).set(field(Question::getId), 2L).create();
		Question q3 = Instancio.of(Question.class).set(field(Question::getId), 3L).create();
		Question q4 = Instancio.of(Question.class).set(field(Question::getId), 4L).create();
		Question q5 = Instancio.of(Question.class).set(field(Question::getId), 5L).create();
		Question q6 = Instancio.of(Question.class).set(field(Question::getId), 6L).create();

		List<Question> all = List.of(q1, q2, q3, q4, q5, q6);

		// userStats: q1,q2 need review (nextReviewAt before now), q3 known (after now)
		UserQuestionStats s1 = Instancio.of(UserQuestionStats.class)
				.set(field(UserQuestionStats::getQuestionId), 1L)
				.set(field(UserQuestionStats::getNextReviewAt), now.minusDays(1))
				.create();
		UserQuestionStats s2 = Instancio.of(UserQuestionStats.class)
				.set(field(UserQuestionStats::getQuestionId), 2L)
				.set(field(UserQuestionStats::getNextReviewAt), now.minusHours(1))
				.create();
		UserQuestionStats s3 = Instancio.of(UserQuestionStats.class)
				.set(field(UserQuestionStats::getQuestionId), 3L)
				.set(field(UserQuestionStats::getNextReviewAt), now.plusDays(1))
				.create();

		List<UserQuestionStats> stats = List.of(s1, s2, s3);

		QuestionSelector selector = new QuestionSelector(props, currentTime);

		// When
		List<Question> selected = selector.selectQuestionsAdaptively(all, stats, 5);

		// Then
		assertEquals(5, selected.size());
		List<Long> ids = selected.stream().map(Question::getId).toList();
		assertTrue(ids.contains(1L));
		assertTrue(ids.contains(2L));
	}
}


