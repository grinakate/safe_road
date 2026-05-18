package ru.itmo.saferoad.gamification.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.gamification.domain.Avatar;
import ru.itmo.saferoad.gamification.domain.repository.AvatarRepository;
import ru.itmo.saferoad.gamification.service.AvatarService;

import java.util.List;

@Service
@AllArgsConstructor
public class AvatarServiceImpl implements AvatarService {

	private final AvatarRepository repository;

	@NotNull
	@Override
	public Avatar existingById(@NotNull Integer id) {
		return repository.findById(id).orElseThrow(
				() -> new IllegalArgumentException("Avatar with id " + id + " not found")
		);
	}

	@NotNull
	@Override
	public List<Avatar> findAll() {
		return repository.findAll();
	}
}
