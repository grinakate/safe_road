package ru.itmo.saferoad.content.api;

import com.example.content.dto.AnswerDto;
import com.example.content.dto.CreateQuestionRequest;
import com.example.content.dto.CreateSectionRequest;
import com.example.content.dto.CreateTopicRequest;
import com.example.content.dto.QuestionWithAnswersDto;
import com.example.content.dto.SectionTreeDto;
import com.example.content.dto.TopicBriefDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionType;
import ru.itmo.saferoad.content.service.AnswerService;
import ru.itmo.saferoad.content.service.QuestionService;
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.content.service.TopicService;
import ru.itmo.saferoad.core.security.AppUserDetails;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/content")
public class ContentController {

	private final SectionService sectionService;
	private final TopicService topicService;
	private final QuestionService questionService;
	private final AnswerService answerService;

	@GetMapping("/sections")
	@PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
	public ResponseEntity<List<SectionTreeDto>> getSectionTree(@AuthenticationPrincipal AppUserDetails currentUser) {
		List<SectionTreeDto> response = sectionService.getAllOrdered().stream()
				.map(section -> {
					List<TopicBriefDto> topics = topicService.getBySectionId(section.getId()).stream()
							.map(topic -> new TopicBriefDto(
									topic.getId(),
									topic.getName(),
									topic.getDescription(),
									topic.getOrderIndex(),
									topic.getXpReward()
							))
							.toList();
					return new SectionTreeDto(section.getId(), section.getName(), section.getOrderIndex(), topics);
				})
				.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/topics/{topicId}/questions")
	@PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
	public ResponseEntity<List<QuestionWithAnswersDto>> getQuestionsByTopic(
			@PathVariable Integer topicId,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		List<QuestionWithAnswersDto> response = questionService.getByTopicId(topicId).stream()
				.map(question -> new QuestionWithAnswersDto(
						question.getId(),
						question.getType().name(),
						question.getDifficultyLevel(),
						question.getContent(),
						answerService.getByQuestionId(question.getId()).stream()
								.map(answer -> new AnswerDto(answer.getId(), answer.getText(), answer.getFeedback()))
								.toList()
				))
				.toList();
		return ResponseEntity.ok(response);
	}

	@PostMapping("/sections")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SectionTreeDto> createSection(
			@Valid @RequestBody CreateSectionRequest request,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var section = sectionService.create(request.name(), request.orderIndex());
		var response = new SectionTreeDto(section.getId(), section.getName(), section.getOrderIndex(), List.of());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/topics")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<TopicBriefDto> createTopic(
			@Valid @RequestBody CreateTopicRequest request,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var topic = topicService.create(
				request.sectionId(),
				request.name(),
				request.description(),
				request.orderIndex(),
				request.xpReward()
		);
		var response = new TopicBriefDto(
				topic.getId(),
				topic.getName(),
				topic.getDescription(),
				topic.getOrderIndex(),
				topic.getXpReward()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/questions")
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
				created.getType().name(),
				created.getDifficultyLevel(),
				created.getContent(),
				answers.stream()
						.map(answer -> new AnswerDto(answer.getId(), answer.getText(), answer.getFeedback()))
						.collect(Collectors.toList())
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}



