package ru.itmo.saferoad.learning.api;

import jakarta.validation.Valid;
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
import ru.itmo.saferoad.learning.dto.SectionUserMapResponse;
import ru.itmo.saferoad.learning.dto.SubmitAnswerRequest;
import ru.itmo.saferoad.learning.dto.SubmitAnswerResponse;
import ru.itmo.saferoad.learning.service.UserQuestionStatsService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learning")
public class LearningController {

	private final MapService mapService;
	private final UserQuestionStatsService userQuestionStatsService;

	@GetMapping("/map")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<SectionUserMapResponse>> getUserMap(
			@AuthenticationPrincipal AppUserDetails currentUser) {
		return ResponseEntity.ok(mapService.getMapForUser(currentUser.getId()));
	}

	@PostMapping("/questions/{questionId}/submit")
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
	}
}


