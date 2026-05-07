package ru.itmo.saferoad.auth.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.saferoad.auth.dto.level.LevelResponse;
import ru.itmo.saferoad.auth.mapper.LevelMapper;
import ru.itmo.saferoad.auth.service.LevelService;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/level")
public class LevelController {

	private final LevelMapper levelMapper;
	private final LevelService levelService;

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<LevelResponse>> listAll() {
		var levels = levelService.getAll();
		var response = levelMapper.mapToResponse(levels);
		return ResponseEntity.ok(response);
	}
}
