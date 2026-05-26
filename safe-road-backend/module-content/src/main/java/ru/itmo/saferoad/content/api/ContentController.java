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
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.content.service.TopicService;
import ru.itmo.saferoad.core.security.AppUserDetails;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/content")
public class ContentController {

	private final SectionService sectionService;
	private final TopicService topicService;
	private final ContentMapper contentMapper;

	@GetMapping("/sections/{id}/topics")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<@NonNull List<TopicBriefDto>> getTopicsForSection(@PathVariable Integer id) {
		return ResponseEntity.ok(contentMapper.toTopicBriefDtoList(topicService.getBySectionId(id)));
	}

	@GetMapping("/topics/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<@NonNull TopicBriefDto> getTopic(@PathVariable Integer id) {
		var topic = topicService.existingById(id);
		return ResponseEntity.ok(contentMapper.toTopicBriefDto(topic));
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

	@GetMapping("/sections")
	@PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
	public ResponseEntity<@NonNull List<SectionTreeDto>> getSectionTree(@AuthenticationPrincipal AppUserDetails currentUser) {
		return ResponseEntity.ok(contentMapper.toSectionTreeDtoList(sectionService.getAllOrdered()));
	}

	@PostMapping("/admin/sections")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<SectionTreeDto> createSection(
			@Valid @RequestBody CreateSectionRequest request,
			@AuthenticationPrincipal AppUserDetails currentUser
	) {
		var section = sectionService.create(request.getName(), request.getOrderIndex());
		var response = contentMapper.toSectionTreeDto(section);
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
		return ResponseEntity.status(HttpStatus.CREATED).body(contentMapper.toTopicBriefDto(topic));
	}
}
