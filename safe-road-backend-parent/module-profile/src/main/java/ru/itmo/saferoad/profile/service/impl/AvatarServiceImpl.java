package ru.itmo.saferoad.profile.service.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.profile.domain.Avatar;
import ru.itmo.saferoad.profile.domain.repository.AvatarRepository;
import ru.itmo.saferoad.profile.service.AvatarService;

@Service
@AllArgsConstructor
public class AvatarServiceImpl implements AvatarService {

	private final AvatarRepository repository;

	@NonNull
	@Override
	public Avatar existingById(@NonNull Integer id) {
		return repository.findById(id).orElseThrow(
				() -> new IllegalArgumentException("Avatar with id " + id + " does not exist"));
	}
}
