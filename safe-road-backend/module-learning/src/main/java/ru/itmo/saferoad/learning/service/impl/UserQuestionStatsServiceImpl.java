package ru.itmo.saferoad.learning.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;
import ru.itmo.saferoad.learning.domain.UserQuestionStatsId;
// ...existing imports...
import ru.itmo.saferoad.learning.domain.repository.UserQuestionStatsRepository;
import ru.itmo.saferoad.learning.dto.SubmitAnswerResponse;
import ru.itmo.saferoad.learning.service.UserQuestionStatsService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserQuestionStatsServiceImpl implements UserQuestionStatsService {

	private final UserQuestionStatsRepository userQuestionStatsRepository;
	private final ReviewScheduler reviewScheduler;
	private final QuestionRepository questionRepository;
	private final CurrentTime currentTime;

	@Override
	@Transactional
	public @NonNull SubmitAnswerResponse submitAnswer(@NonNull Long userId, @NonNull Long questionId, @NonNull Integer answerId) {
		Question question = questionRepository.findById(questionId)
				.orElseThrow(() -> new IllegalArgumentException("Question not found"));

		boolean isCorrect = question.getContent().getOptions().stream()
				.filter(opt -> opt.getNumber().equals(answerId))
				.map(QuestionContent.AnswerOption::getIsCorrect)
				.findFirst()
				.orElse(false);

		UserQuestionStatsId statsId = new UserQuestionStatsId();
		statsId.setUserId(userId);
		statsId.setQuestionId(questionId);

		UserQuestionStats stats = userQuestionStatsRepository.findById(statsId)
				.orElseGet(() -> {
					UserQuestionStats newStats = new UserQuestionStats();
					newStats.setUserId(userId);
					newStats.setQuestionId(questionId);
					newStats.setTopicId(question.getTopic().getId());
					newStats.setSuccessStreak(0);
					return newStats;
				});

		stats.setLastResultCorrect(isCorrect);

		int newStreak = isCorrect ? stats.getSuccessStreak() + 1 : 0;
		stats.setSuccessStreak(newStreak);

		int hours = reviewScheduler.getHoursForStreak(newStreak, isCorrect);
		stats.setNextReviewAt(currentTime.nowDateTime().plusHours(hours));
		userQuestionStatsRepository.save(stats);

		return new SubmitAnswerResponse(isCorrect, isCorrect ? 10 : 0);
	}
}
