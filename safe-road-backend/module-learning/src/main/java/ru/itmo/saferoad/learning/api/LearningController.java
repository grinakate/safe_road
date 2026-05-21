package ru.itmo.saferoad.learning.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.learning.dto.StartTestRequest;
import ru.itmo.saferoad.learning.dto.StartTestResponse;
import ru.itmo.saferoad.learning.dto.SubmitSessionAnswerRequest;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;
import ru.itmo.saferoad.learning.dto.TestResultResponse;
import ru.itmo.saferoad.learning.dto.TestSessionSubmitResponse;
import ru.itmo.saferoad.learning.dto.UserRoadMapResponse;
import ru.itmo.saferoad.learning.service.TestSessionService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/learning")
public class LearningController {

	private final MapService mapService;
	private final TestSessionService testSessionService;

	@PostMapping("/test/start")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<StartTestResponse> startTest(@RequestBody StartTestRequest request,
													   @AuthenticationPrincipal AppUserDetails user) {
		return ResponseEntity.ok(testSessionService.startTest(request, user.getId()));
	}

	@GetMapping("/test/{sessionId}/questions")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<TestQuestionResponse>> getTestQuestions(@PathVariable Integer sessionId,
																	   @AuthenticationPrincipal AppUserDetails user) {
		return ResponseEntity.ok(testSessionService.getTestQuestions(sessionId, user.getId()));
	}

	@PostMapping("/test/{sessionId}/submit-answer")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<TestSessionSubmitResponse> submitSessionAnswer(
			@PathVariable Integer sessionId,
			@RequestBody SubmitSessionAnswerRequest request,
			@AuthenticationPrincipal AppUserDetails user) {
		return ResponseEntity.ok(testSessionService.submitSessionAnswer(sessionId, request, user.getId()));
	}

	@GetMapping("/test/{sessionId}/result")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<TestResultResponse> getTestResult(@PathVariable Integer sessionId,
															@AuthenticationPrincipal AppUserDetails user) {
		return ResponseEntity.ok(testSessionService.getTestResult(sessionId, user.getId()));
	}

	@GetMapping("/statistics")
	public ResponseEntity<?> getStatistics() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@GetMapping("/roadmap")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<UserRoadMapResponse>> getUserMap(@AuthenticationPrincipal AppUserDetails user) {
		return ResponseEntity.ok(mapService.getMapForUser(user.getId()));
	}

/*	@PostMapping("/questions/{questionId}/submit")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<SubmitAnswerResponse> submitAnswer(
			@PathVariable Long questionId,
			@Valid @RequestBody SubmitAnswerRequest request,
			@AuthenticationPrincipal AppUserDetails user
	) {
		SubmitAnswerResponse response = userQuestionStatsService.submitAnswer(
				user.getId(),
				questionId,
				request.answerId()
		);
		return ResponseEntity.ok(response);
	}*/

/*	@GetMapping("/topics/{topicId}/quiz")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<QuestionResponse>> getQuiz(@PathVariable Integer topicId,
														  @AuthenticationPrincipal AppUserDetails user) {
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
