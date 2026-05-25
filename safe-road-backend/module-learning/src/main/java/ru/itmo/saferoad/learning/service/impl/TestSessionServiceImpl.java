package ru.itmo.saferoad.learning.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.content.service.TopicService;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.learning.config.LearningProperties;
import ru.itmo.saferoad.learning.domain.TestMode;
import ru.itmo.saferoad.learning.domain.TestSession;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;
import ru.itmo.saferoad.learning.domain.repository.TestSessionRepository;
import ru.itmo.saferoad.learning.domain.repository.UserQuestionStatsRepository;
import ru.itmo.saferoad.learning.dto.StartTestRequest;
import ru.itmo.saferoad.learning.dto.StartTestResponse;
import ru.itmo.saferoad.learning.dto.SubmitSessionAnswerRequest;
import ru.itmo.saferoad.learning.dto.TestAnswerOptionResponse;
import ru.itmo.saferoad.learning.dto.TestErrorResponse;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;
import ru.itmo.saferoad.learning.dto.TestResultResponse;
import ru.itmo.saferoad.learning.dto.TestSessionSubmitResponse;
import ru.itmo.saferoad.learning.service.TestSessionService;
import ru.itmo.saferoad.learning.service.UserQuestionStatsService;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestSessionServiceImpl implements TestSessionService {

	private final TestSessionRepository testSessionRepository;
	private final UserQuestionStatsService userQuestionStatsService;
	private final CreatePendingEventsService createPendingEventsService;
	private final QuestionRepository questionRepository;
	private final UserQuestionStatsRepository userQuestionStatsRepository;
	private final LearningProperties learningProperties;
	private final TopicService topicService;
	private final UserTopicProgressService userTopicProgressService;
	private final CurrentTime currentTime;

	@Override
	@Transactional
	public StartTestResponse startTest(StartTestRequest request, Long userId) {
		TestSession session = new TestSession();
		session.setUserId(userId);
		session.setStatus(ProgressStatus.IN_PROGRESS);
		session.setTopicId(request.getTopicId());
		session.setSectionId(request.getSectionId());

		List<Question> selectedQuestions;
		if (request.getTopicId() != null) {
			session.setMode(TestMode.TOPIC);
			selectedQuestions = selectQuestionsByTopic(userId, request.getTopicId());
		} else if (request.getSectionId() != null) {
			session.setMode(TestMode.SECTION);
			selectedQuestions = selectQuestionsBySection(userId, request.getSectionId());
		} else {
			throw new IllegalArgumentException("Invalid request");
		}

		var totalQuestions = selectedQuestions.size();

		session.setTotalQuestions(totalQuestions);
		session.setCorrectCount(0);
		session.setCreatedAt(currentTime.nowDateTime());

		Map<String, Object> qData = new HashMap<>();
		qData.put("questionIds", selectedQuestions.stream().map(Question::getId).toList());
		session.setQuestionsData(qData);

		TestSession saved = testSessionRepository.save(session);
		return new StartTestResponse(saved.getId(), saved.getTotalQuestions());
	}

	@Override
	public List<TestQuestionResponse> getTestQuestions(Integer sessionId, Long userId) {
		TestSession session = testSessionRepository.findById(sessionId)
				.orElseThrow(() -> new RuntimeException("Session not found"));

		List<Long> questionIds = (List<Long>) session.getQuestionsData().get("questionIds");
		if (questionIds == null) {
			return Collections.emptyList();
		}

		List<Question> questions = questionRepository.findAllById(questionIds);

		return questions.stream().map(q -> {
			Integer correctAnswerNumber = q.getContent().getOptions().stream()
					.filter(ru.itmo.saferoad.content.domain.QuestionContent.AnswerOption::getIsCorrect)
					.findFirst()
					.map(ru.itmo.saferoad.content.domain.QuestionContent.AnswerOption::getNumber)
					.orElse(null);

			List<TestAnswerOptionResponse> options = q.getContent().getOptions().stream()
					.map(o -> new TestAnswerOptionResponse(
							o.getNumber(),
							o.getText(),
							o.getFeedback(),
							o.getIsCorrect()))
					.toList();

			return new TestQuestionResponse(
					q.getId(),
					q.getType(),
					q.getContent().getQuestionText(),
					q.getContent().getImageUrlPlaceholder(),
					options,
					correctAnswerNumber
			);
		}).toList();
	}

	@NotNull
	private List<Question> selectQuestionsByTopic(@NotNull Long userId, @NotNull Integer topicId) {
		List<Question> allQuestions = questionRepository.findByTopicId(topicId);
		List<UserQuestionStats> userStats = userQuestionStatsRepository.findByUserIdAndTopicId(userId, topicId);
		int targetCount = 10;
		return selectQuestionsAdaptively(allQuestions, userStats, targetCount);
	}

	@NotNull
	private List<Question> selectQuestionsBySection(@NotNull Long userId, @NotNull Integer sectionId) {
		List<Question> allQuestions = questionRepository.findByTopicSectionId(sectionId);
		List<UserQuestionStats> userStats = userQuestionStatsRepository.findByUserIdAndSectionId(userId, sectionId);
		int targetCount = 20;
		return selectQuestionsAdaptively(allQuestions, userStats, targetCount);
	}

	@NotNull
	private List<Question> selectQuestionsAdaptively(@NotNull List<Question> allQuestions,
													 @NotNull List<UserQuestionStats> userStats,
													 int targetCount) {
		LocalDateTime now = currentTime.nowDateTime();

		Map<Long, UserQuestionStats> statsMap = new HashMap<>();
		for (UserQuestionStats stat : userStats) {
			statsMap.put(stat.getQuestionId(), stat);
		}

		List<Question> needsReview = new ArrayList<>();
		List<Question> newQuestions = new ArrayList<>();
		List<Question> knownQuestions = new ArrayList<>();

		for (Question q : allQuestions) {
			UserQuestionStats stat = statsMap.get(q.getId());
			if (stat == null) {
				newQuestions.add(q);
			} else if (!stat.getNextReviewAt().isAfter(now)) {
				needsReview.add(q);
			} else {
				knownQuestions.add(q);
			}
		}

		needsReview.sort(Comparator.comparing(q -> statsMap.get(q.getId()).getNextReviewAt()));
		Collections.shuffle(newQuestions);
		Collections.shuffle(knownQuestions);

		int maxReview = (int) Math.floor(targetCount * learningProperties.getReviewRatio());
		int maxNew = (int) Math.floor(targetCount * learningProperties.getNewRatio());
		int maxKnown = targetCount - (maxReview + maxNew);

		// 1) 60% Review
		int actualReview = Math.min(maxReview, needsReview.size());
		List<Question> selected = new ArrayList<>(needsReview.subList(0, actualReview));
		int shortageReview = maxReview - actualReview;

		// 2) 30% (+ Review shortage) New
		int targetNew = maxNew + shortageReview;
		int actualNew = Math.min(targetNew, newQuestions.size());
		selected.addAll(newQuestions.subList(0, actualNew));
		int shortageNew = targetNew - actualNew;

		// 3) 10% (+ New shortage) Known
		int targetKnown = maxKnown + shortageNew;
		int actualKnown = Math.min(targetKnown, knownQuestions.size());
		selected.addAll(knownQuestions.subList(0, actualKnown));
		int shortageKnown = targetKnown - actualKnown;

		// 4) If still short, try going back and pulling from what's left
		if (shortageKnown > 0) {
			int remainingReviewTarget = shortageKnown;
			int remainingReviewActual = Math.min(remainingReviewTarget, needsReview.size() - actualReview);
			selected.addAll(needsReview.subList(actualReview, actualReview + remainingReviewActual));
			shortageKnown -= remainingReviewActual;

			if (shortageKnown > 0) {
				int remainingNewActual = Math.min(shortageKnown, newQuestions.size() - actualNew);
				selected.addAll(newQuestions.subList(actualNew, actualNew + remainingNewActual));
			}
		}

		return selected;
	}

	@Override
	@Transactional
	public TestSessionSubmitResponse submitSessionAnswer(@NotNull Integer sessionId,
														 @NotNull SubmitSessionAnswerRequest request,
														 @NotNull Long userId) {
		TestSession session = testSessionRepository.findById(sessionId)
				.orElseThrow(() -> new RuntimeException("Session not found"));

		if (!session.getUserId().equals(userId)) {
			throw new RuntimeException("Invalid session");
		}

		if (session.getStatus() == ProgressStatus.COMPLETED) {
			throw new RuntimeException("Session already completed");
		}

		int correctAnswers = 0;
		List<TestErrorResponse> errors = new ArrayList<>();
		List<TestSessionCompletedEvent.QuestionDetail> details = new ArrayList<>();

		if (request.getAnswers() != null) {
			for (var entry : request.getAnswers().entrySet()) {
				Long questionId = entry.getKey();
				Integer answerId = entry.getValue();

				var response = userQuestionStatsService.submitAnswer(userId, questionId, answerId);
				Question q = questionRepository.findById(questionId).orElseThrow();

				if (response.isCorrect()) {
					correctAnswers++;
					details.add(TestSessionCompletedEvent.QuestionDetail.builder()
							.type(q.getType().name())
							.difficulty(q.getDifficultyLevel())
							.isCorrect(true)
							.build());
				} else {
					details.add(TestSessionCompletedEvent.QuestionDetail.builder()
							.type(q.getType().name())
							.difficulty(q.getDifficultyLevel())
							.isCorrect(false)
							.build());

					String questionText = q.getContent().getQuestionText();
					QuestionContent.AnswerOption correctAnswer = q.getContent().getOptions().stream()
							.filter(QuestionContent.AnswerOption::getIsCorrect)
							.findFirst()
							.orElseThrow();

					QuestionContent.AnswerOption userAnswer = q.getContent().getOptions().stream()
							.filter(o -> o.getNumber().equals(answerId))
							.findFirst()
							.orElseThrow();
					errors.add(new TestErrorResponse(
							questionText,
							userAnswer.getText(),
							correctAnswer.getText(),
							userAnswer.getFeedback()));
				}
			}
		}

		session.setCorrectCount(correctAnswers);
		session.setStatus(ProgressStatus.COMPLETED);
		session.setFinishedAt(currentTime.nowDateTime());
		testSessionRepository.save(session);

		TestSessionCompletedEvent eventPayload = TestSessionCompletedEvent.builder()
				.userId(userId)
				.topicId(session.getTopicId())
				.totalQuestions(session.getTotalQuestions())
				.correctAnswers(correctAnswers)
				.details(details)
				.build();
		createPendingEventsService.publishTestSessionCompletedEvent(eventPayload);

		if (correctAnswers >= session.getTotalQuestions() * 0.8 && session.getTopicId() != null) {
			Topic currentTopic = topicService.existingById(session.getTopicId());
			userTopicProgressService.updateProgress(userId, currentTopic.getId(), ProgressStatus.COMPLETED);
			Optional<Topic> nextTopicOpt = topicService.getNextTopic(currentTopic);
			nextTopicOpt.ifPresent(nextTopic -> userTopicProgressService.unlockTopic(userId, nextTopic));
		}

		return new TestSessionSubmitResponse(session.getTotalQuestions(), correctAnswers, errors);
	}

	@Override
	public TestResultResponse getTestResult(Integer sessionId, Long userId) {
		TestSession session = testSessionRepository.findById(sessionId)
				.orElseThrow(() -> new RuntimeException("Session not found"));

		return new TestResultResponse(
				session.getCorrectCount(),
				session.getTotalQuestions(),
				Collections.emptyList()
		);
	}
}
