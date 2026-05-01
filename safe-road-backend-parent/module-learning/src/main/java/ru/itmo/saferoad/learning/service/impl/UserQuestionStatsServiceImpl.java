package ru.itmo.saferoad.learning.service.impl;

import com.example.learning.dto.SubmitAnswerResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.content.service.AnswerService;
import ru.itmo.saferoad.content.service.QuestionService;
import ru.itmo.saferoad.content.service.TopicService;
import ru.itmo.saferoad.gamification.service.UserMetricService;
import ru.itmo.saferoad.learning.domain.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;
import ru.itmo.saferoad.learning.domain.UserQuestionStatsId;
import ru.itmo.saferoad.learning.domain.repository.UserQuestionStatsRepository;
import ru.itmo.saferoad.learning.service.ReviewIntervalService;
import ru.itmo.saferoad.learning.service.UserSectionProgressService;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;
import ru.itmo.saferoad.learning.service.UserQuestionStatsService;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuestionStatsServiceImpl implements UserQuestionStatsService {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final TopicService topicService;
	private final UserMetricService userMetricService;
	private final ReviewIntervalService reviewIntervalService;
	private final UserTopicProgressService userTopicProgressService;
	private final UserSectionProgressService userSectionProgressService;
	private final UserQuestionStatsRepository userQuestionStatsRepository;

	@Override
	@Transactional
	public @NonNull SubmitAnswerResponse submitAnswer(@NonNull Long userId,
	                                                  @NonNull Long questionId,
	                                                  @NonNull Integer answerId) {
		var question = questionService.existingById(questionId);
		var answer = answerService.existingById(answerId);
		if (!answer.getQuestion().getId().equals(questionId)) {
			throw new IllegalArgumentException("Answer does not belong to question " + questionId);
		}

		boolean isCorrect = Boolean.TRUE.equals(answer.getIsCorrect());
		UserQuestionStats stats = userQuestionStatsRepository.findById(new UserQuestionStatsId(userId, questionId))
				.orElseGet(UserQuestionStats::new);

		stats.setUserId(userId);
		stats.setQuestionId(questionId);
		stats.setTopicId(question.getTopic().getId());
		stats.setLastResultCorrect(isCorrect);

		int newStreak = isCorrect ? stats.getSuccessStreak() + 1 : 0;
		stats.setSuccessStreak(newStreak);

		int intervalLevel = Math.min(5, Math.max(1, newStreak));
		int intervalHours = reviewIntervalService.existingByStreakLevel(intervalLevel).getIntervalHours();
		stats.setNextReviewAt(LocalDateTime.now().plusHours(intervalHours));
		userQuestionStatsRepository.save(stats);

		updateProgress(userId, question.getTopic().getId(), question.getTopic().getSection().getId());

		int awardedXp = isCorrect ? userMetricService.addXp(userId, question.getDifficultyLevel()) : 0;
		return new SubmitAnswerResponse(isCorrect, awardedXp);
	}

	private void updateProgress(Long userId, Integer topicId, Integer sectionId) {
		long totalQuestions = questionService.countByTopicId(topicId);
		long masteredQuestions = userQuestionStatsRepository.findByUserIdAndTopicId(userId, topicId).stream()
				.filter(item -> item.getSuccessStreak() > 0)
				.map(UserQuestionStats::getQuestionId)
				.distinct()
				.count();

		ProgressStatus topicStatus = (totalQuestions > 0 && masteredQuestions >= totalQuestions)
				? ProgressStatus.COMPLETED
				: ProgressStatus.UNLOCKED;
		userTopicProgressService.upsertStatus(userId, topicId, topicStatus);

		Set<Integer> completedTopicIds = userTopicProgressService.getByUserId(userId).stream()
				.filter(progress -> progress.getStatus() == ProgressStatus.COMPLETED)
				.map(progress -> progress.getTopicId())
				.collect(Collectors.toSet());

		boolean allTopicsCompleted = topicService.getBySectionId(sectionId).stream()
				.map(topic -> topic.getId())
				.allMatch(completedTopicIds::contains);

		userSectionProgressService.upsertStatus(
				userId,
				sectionId,
				allTopicsCompleted ? ProgressStatus.COMPLETED : ProgressStatus.UNLOCKED
		);
	}
}
