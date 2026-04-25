package ru.itmo.saferoad.services.business;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.domain.entity.Avatar;
import ru.itmo.saferoad.domain.exception.BusinessException;
import ru.itmo.saferoad.domain.repository.AvatarRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class AvatarService {

	private final AvatarRepository repository;

	@NotNull
	public Avatar existingById(long id) {
		return repository.findById(id)
				.orElseThrow(() -> new BusinessException("Avatar Not Found"));
	}

	@NotNull
	public List<Avatar> getAll() {
		return repository.findAll();
	}
}
