package ru.itmo.saferoad.learning.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.content.service.QuestionService;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.learning.dto.UserRoadMapResponse;
import ru.itmo.saferoad.learning.service.UserQuestionStatsService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/learning")
public class LearningController {

	@PostMapping("/test/start")
	public ResponseEntity<?> startTest() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/test/{sessionId}/questions")
	public ResponseEntity<?> getTestQuestions(@PathVariable Long sessionId) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PostMapping("/test/{sessionId}/submit-answer")
	public ResponseEntity<?> submitSessionAnswer(@PathVariable Long sessionId) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/test/{sessionId}/result")
	public ResponseEntity<?> getTestResult(@PathVariable Long sessionId) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/statistics")
	public ResponseEntity<?> getStatistics() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	private final MapService mapService;
	private final QuestionService questionService;
	private final UserQuestionStatsService userQuestionStatsService;

	@GetMapping("/roadmap")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<UserRoadMapResponse>> getUserMap(
			@AuthenticationPrincipal AppUserDetails currentUser) {
		return ResponseEntity.ok(mapService.getMapForUser(currentUser.getId()));
	}

/*	@PostMapping("/questions/{questionId}/submit")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<SubmitAnswerResponse> submitAnswer(
			@PathVariable Long questionId,
			@Valid @RequestBody SubmitAnswerRequest request,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		SubmitAnswerResponse response = userQuestionStatsService.submitAnswer(
				currentUser.getId(),
				questionId,
				request.answerId()
		);
		return ResponseEntity.ok(response);
	}*/

/*	@GetMapping("/topics/{topicId}/quiz")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<QuestionResponse>> getQuiz(@PathVariable Integer topicId,
														  @AuthenticationPrincipal AppUserDetails currentUser) {
		var questions = questionService.getByTopicId(topicId).stream()
				.map(question -> new QuestionResponse(
						question.getId(),
						question.getContent().getQuestionText(),
						question.getAnswers().stream()
								.map(answer -> new AnswerResponse(
										answer.getId(),
										answer.getText(),
										answer.getFeedback(),
										answer.getIsCorrect()))
								.toList(),
						question.getAnswers().stream()
								.filter(Answer::getIsCorrect)
								.findFirst().orElseThrow().getId()
				))
				.toList();
		return ResponseEntity.ok(questions);
	}*/
}


