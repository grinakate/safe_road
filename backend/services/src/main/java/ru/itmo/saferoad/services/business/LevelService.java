package ru.itmo.saferoad.services.business;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.domain.entity.Level;
import ru.itmo.saferoad.domain.exception.BusinessException;
import ru.itmo.saferoad.domain.repository.LevelRepository;

@Service
@AllArgsConstructor
public class LevelService {

	private final LevelRepository repository;

	@NotNull
	public Level existingByNumber(int number) {
		return repository.findByNumber(number)
				.orElseThrow(() -> new BusinessException("Level Not Found"));
	}
}
