package ru.itmo.saferoad.profile.service.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.profile.domain.Level;
import ru.itmo.saferoad.profile.domain.repository.LevelRepository;
import ru.itmo.saferoad.profile.service.LevelService;

@Service
@AllArgsConstructor
public class LevelServiceImpl implements LevelService {

	private final LevelRepository repository;

	@NonNull
	@Override
	public Level existingByNumber(@NonNull Integer number) {
		return repository.findById(number)
				.orElseThrow(() -> new IllegalArgumentException("Level with number " + number + " does not exist"));
	}
}
