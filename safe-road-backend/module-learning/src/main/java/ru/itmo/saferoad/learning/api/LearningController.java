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
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.learning.domain.repository.UserQuestionStatsRepository;
import ru.itmo.saferoad.learning.dto.SectionStatisticsResponse;
import ru.itmo.saferoad.learning.dto.StartTestRequest;
import ru.itmo.saferoad.learning.dto.StartTestResponse;
import ru.itmo.saferoad.learning.dto.SubmitSessionAnswerRequest;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;
import ru.itmo.saferoad.learning.dto.TestResultResponse;
import ru.itmo.saferoad.learning.dto.TestSessionSubmitResponse;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;
import ru.itmo.saferoad.learning.service.TestSessionService;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;
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

	@GetMapping("/me/statistics")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<SectionStatisticsResponse>> getStatistics(@AuthenticationPrincipal AppUserDetails user) {
		var userId = user.getId();
		return ResponseEntity.ok(statisticsService.getStatisticsForUser(userId));
	}

	@GetMapping("/roadmap")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<UserSectionResponse>> getRoadMap(@AuthenticationPrincipal AppUserDetails user) {
		return ResponseEntity.ok(mapService.getMapForUser(user.getId()));
	}
}
