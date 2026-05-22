package ru.itmo.saferoad.content.api;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.content.dto.CreateSectionRequest;
import ru.itmo.saferoad.content.dto.CreateTopicRequest;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.content.dto.TopicBriefDto;
import ru.itmo.saferoad.content.service.QuestionService;
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.content.service.TopicService;
import ru.itmo.saferoad.core.security.AppUserDetails;
import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/content")
public class ContentController {

	@GetMapping("/sections/{id}/topics")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<@NonNull List<TopicBriefDto>> getTopicsForSection(@PathVariable Integer id) {
		List<TopicBriefDto> topics = topicService.getBySectionId(id).stream()
				.map(topic -> new TopicBriefDto(
						topic.getId(),
						topic.getTitle(),
						topic.getContent(),
						topic.getOrderIndex()
					))
				.toList();
		return ResponseEntity.ok(topics);
	}

	@GetMapping("/topics/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<@NonNull TopicBriefDto> getTopic(@PathVariable Integer id) {
		var topic = topicService.existingById(id);
		var dto = new TopicBriefDto(
				topic.getId(),
				topic.getTitle(),
				topic.getContent(),
				topic.getOrderIndex()
		);
		return ResponseEntity.ok(dto);
	}

	@PutMapping("/admin/sections/{id}")
	public ResponseEntity<?> updateSection(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/admin/sections/{id}/archive")
	public ResponseEntity<?> archiveSection(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/topics/{id}")
	public ResponseEntity<?> updateTopic(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/admin/topics/{id}/archive")
	public ResponseEntity<?> archiveTopic(@PathVariable Integer id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PutMapping("/admin/questions/{id}")
	public ResponseEntity<?> updateQuestion(@PathVariable Long id) {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	@PostMapping("/admin/media/upload")
	public ResponseEntity<?> uploadMedia() {
		// TODO: Implement
		return ResponseEntity.ok().build();
	}

	private final SectionService sectionService;
	private final TopicService topicService;
	private final QuestionService questionService;

	@GetMapping("/sections")
	@PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
	public ResponseEntity<@NonNull List<SectionTreeDto>> getSectionTree(@AuthenticationPrincipal AppUserDetails currentUser) {
		List<SectionTreeDto> response = sectionService.getAllOrdered().stream()
				.map(section -> {
					List<TopicBriefDto> topics = topicService.getBySectionId(section.getId()).stream()
							.map(topic -> new TopicBriefDto(
									topic.getId(),
									topic.getTitle(),
									topic.getContent(),
									topic.getOrderIndex()
							))
							.toList();
					return new SectionTreeDto(section.getId(), section.getTitle(), section.getOrderIndex(), topics);
				})
				.toList();
		return ResponseEntity.ok(response);
	}

/*	@GetMapping("/topics/{topicId}/questions")
	@PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
	public ResponseEntity<List<QuestionWithAnswersDto>> getQuestionsByTopic(
			@PathVariable Integer topicId,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		List<QuestionWithAnswersDto> response = questionService.getByTopicId(topicId).stream()
				.map(question -> new QuestionWithAnswersDto(
						question.getId(),
						question.getType().title(),
						question.getDifficultyLevel(),
						question.getContent(),
						answerService.getByQuestionId(question.getId()).stream()
								.map(answer -> new AnswerDto(answer.getId(), answer.getText(), answer.getFeedback()))
								.toList()
				))
				.toList();
		return ResponseEntity.ok(response);
	}*/

	@PostMapping("/admin/sections")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SectionTreeDto> createSection(
			@Valid @RequestBody CreateSectionRequest request,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var section = sectionService.create(request.getName(), request.getOrderIndex());
		var response = new SectionTreeDto(section.getId(), section.getTitle(), section.getOrderIndex(), List.of());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/admin/topics")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TopicBriefDto> createTopic(
			@Valid @RequestBody CreateTopicRequest request,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var topic = topicService.create(
				request.getSectionId(),
				request.getName(),
				request.getDescription(),
				request.getOrderIndex(),
				request.getXpReward()
		);
		var response = new TopicBriefDto(
				topic.getId(),
				topic.getTitle(),
				topic.getContent(),
				topic.getOrderIndex()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

/*	@PostMapping("/admin/questions")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<QuestionWithAnswersDto> createQuestion(
			@Valid @RequestBody CreateQuestionRequest request,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		QuestionType type = QuestionType.valueOf(request.type().toUpperCase());
		Question created = questionService.create(
				request.topicId(),
				type,
				request.difficultyLevel(),
				request.content()
		);

		var answers = request.answers().stream()
				.map(answerRequest -> answerService.create(
						created,
						answerRequest.text(),
						answerRequest.isCorrect(),
						answerRequest.feedback()
				))
				.toList();

		var response = new QuestionWithAnswersDto(
				created.getId(),
				created.getType().title(),
				created.getDifficultyLevel(),
				created.getContent(),
				answers.stream()
						.map(answer -> new AnswerDto(answer.getId(), answer.getText(), answer.getFeedback()))
						.collect(Collectors.toList())
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}*/
}
