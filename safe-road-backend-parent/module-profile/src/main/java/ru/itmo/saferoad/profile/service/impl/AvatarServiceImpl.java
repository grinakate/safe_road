package ru.itmo.saferoad.profile.service.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.profile.domain.Avatar;
import ru.itmo.saferoad.profile.domain.repository.AvatarRepository;
import ru.itmo.saferoad.profile.service.AvatarService;

import java.util.List;

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

	@NonNull
	@Override
	public Avatar save(@NonNull Avatar avatar) {
		return repository.save(avatar);
	}

	@Override
	public @NonNull List<Avatar> getAll() {
		return repository.findAll();
	}
}
