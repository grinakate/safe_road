package ru.itmo.saferoad.learning.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionContent;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestSessionServiceImpl implements TestSessionService {

	private final TestSessionRepository testSessionRepository;
	private final UserQuestionStatsService userQuestionStatsService;
	private final CreatePendingEventsService createPendingEventsService;
	private final QuestionRepository questionRepository;
	private final UserQuestionStatsRepository userQuestionStatsRepository;

	@Override
	@Transactional
	public StartTestResponse startTest(StartTestRequest request, Long userId) {
		TestSession session = new TestSession();
		session.setUserId(userId);
		session.setMode(TestMode.PRACTICE); // default mode for now
		session.setStatus(ProgressStatus.IN_PROGRESS);

		int totalQuestions = 10;
		List<Question> selectedQuestions = selectQuestionsAdaptively(userId, request.topicId(), totalQuestions);
		totalQuestions = selectedQuestions.size();

		session.setTotalQuestions(totalQuestions);
		session.setCorrectCount(0);
		session.setCreatedAt(LocalDateTime.now());

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
					.map(o -> new TestAnswerOptionResponse(o.getNumber(), o.getText()))
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

	private List<Question> selectQuestionsAdaptively(Long userId, Integer topicId, int targetCount) {
		List<Question> allQuestions = topicId != null
				? questionRepository.findByTopicId(topicId)
				: questionRepository.findAll();

		List<UserQuestionStats> userStats = topicId != null
				? userQuestionStatsRepository.findByUserIdAndTopicId(userId, topicId)
				: userQuestionStatsRepository.findByUserId(userId);

		LocalDateTime now = LocalDateTime.now();

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

		int maxReview = (int) Math.floor(targetCount * 0.6);
		int maxNew = (int) Math.floor(targetCount * 0.3);
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

		if (request.answers() != null) {
			for (var entry : request.answers().entrySet()) {
				Long questionId = entry.getKey();
				Integer answerId = entry.getValue();

				var response = userQuestionStatsService.submitAnswer(userId, questionId, answerId);
				Question q = questionRepository.findById(questionId).orElseThrow();

				if (response.correct()) {
					correctAnswers++;
					details.add(new TestSessionCompletedEvent.QuestionDetail(
							q.getType().name(),
							q.getDifficultyLevel(),
							true
					));
				} else {
					details.add(new TestSessionCompletedEvent.QuestionDetail(
							q.getType().name(),
							q.getDifficultyLevel(),
							false
					));

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
							correctAnswer.getText(),
							userAnswer.getText(),
							userAnswer.getFeedback()));
				}
			}
		}

		session.setCorrectCount(correctAnswers);
		session.setStatus(ProgressStatus.COMPLETED);
		session.setFinishedAt(LocalDateTime.now());
		testSessionRepository.save(session);

		TestSessionCompletedEvent eventPayload = new TestSessionCompletedEvent(
				userId,
				session.getTopic() != null ? session.getTopic().getId() : null,
				session.getTotalQuestions(),
				correctAnswers,
				details
		);
		createPendingEventsService.publishTestSessionCompletedEvent(eventPayload);

		return new TestSessionSubmitResponse(session.getTotalQuestions(), correctAnswers, errors);
	}

	@Override
	public TestResultResponse getTestResult(Integer sessionId, Long userId) {
		TestSession session = testSessionRepository.findById(sessionId)
				.orElseThrow(() -> new RuntimeException("Session not found"));

		return new TestResultResponse(
				session.getCorrectCount(),
				session.getTotalQuestions(),
				Collections.emptyList() // explanations
		);
	}
}
