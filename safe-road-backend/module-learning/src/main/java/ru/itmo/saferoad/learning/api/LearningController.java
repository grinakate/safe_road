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
import ru.itmo.saferoad.learning.dto.SectionStatisticsResponse;
import ru.itmo.saferoad.learning.dto.StartTestRequest;
import ru.itmo.saferoad.learning.dto.StartTestResponse;
import ru.itmo.saferoad.learning.dto.SubmitSessionAnswerRequest;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;
import ru.itmo.saferoad.learning.dto.TestResultResponse;
import ru.itmo.saferoad.learning.dto.TestSessionSubmitResponse;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;
import ru.itmo.saferoad.learning.service.TestSessionService;
import ru.itmo.saferoad.learning.service.impl.StatisticsService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/learning")
public class LearningController {

	private final MapService mapService;
	private final TestSessionService testSessionService;
	private final StatisticsService statisticsService;

	@PostMapping("/test/start")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<StartTestResponse> startTest(@RequestBody StartTestRequest request,
													   @AuthenticationPrincipal AppUserDetails user) {
		var response = testSessionService.startTest(request, user.getId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/test/{sessionId}/questions")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<TestQuestionResponse>> getTestQuestions(@PathVariable Long sessionId,
																	   @AuthenticationPrincipal AppUserDetails user) {
		var response = testSessionService.getTestQuestions(sessionId, user.getId());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/test/{sessionId}/submit-answer")
	public ResponseEntity<TestSessionSubmitResponse> submitSessionAnswer(
			@PathVariable Long sessionId,
			@RequestBody SubmitSessionAnswerRequest request,
			@AuthenticationPrincipal AppUserDetails user) {
		var response = testSessionService.submitSessionAnswer(sessionId, request, user.getId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/test/{sessionId}/result")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<TestResultResponse> getTestResult(@PathVariable Long sessionId,
															@AuthenticationPrincipal AppUserDetails user) {
		var response = testSessionService.getTestResult(sessionId, user.getId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/me/statistics")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<SectionStatisticsResponse>> getStatistics(@AuthenticationPrincipal AppUserDetails user) {
		var response = statisticsService.getStatisticsForUser(user.getId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/roadmap")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<UserSectionResponse>> getRoadMap(@AuthenticationPrincipal AppUserDetails user) {
		var response = mapService.getMapForUser(user.getId());
		return ResponseEntity.ok(response);
	}
}
