package ru.itmo.saferoad.profile.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.profile.dto.avatar.AvatarCreateRequest;
import ru.itmo.saferoad.profile.dto.avatar.AvatarResponse;
import ru.itmo.saferoad.profile.domain.Avatar;
import ru.itmo.saferoad.profile.mapper.AvatarMapper;
import ru.itmo.saferoad.profile.service.AvatarService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/avatar")
public class AvatarController {

	private final AvatarMapper avatarMapper;
	private final AvatarService avatarService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AvatarResponse> create(@Valid @RequestBody AvatarCreateRequest request) {
		Avatar newAvatar = avatarService.save(avatarMapper.mapToEntity(request));
		AvatarResponse response = avatarMapper.mapToResponse(newAvatar);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<AvatarResponse>> listAll() {
		var avatars = avatarService.getAll();
		var response = avatarMapper.mapToResponse(avatars);
		return ResponseEntity.ok(response);
	}
}
