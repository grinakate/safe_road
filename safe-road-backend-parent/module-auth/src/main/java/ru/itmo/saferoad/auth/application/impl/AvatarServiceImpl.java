package ru.itmo.saferoad.auth.application.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.auth.domain.Avatar;
import ru.itmo.saferoad.auth.domain.repository.AvatarRepository;
import ru.itmo.saferoad.auth.application.AvatarService;

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
