package ru.itmo.saferoad.learning.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.learning.domain.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserSectionProgress;
import ru.itmo.saferoad.learning.domain.UserSectionProgressId;
import ru.itmo.saferoad.learning.domain.repository.UserSectionProgressRepository;
import ru.itmo.saferoad.learning.service.UserSectionProgressService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSectionProgressServiceImpl implements UserSectionProgressService {

	private final UserSectionProgressRepository repository;

	@Override
	public @NonNull List<UserSectionProgress> getByUserId(@NonNull Long userId) {
		return repository.findByUserId(userId);
	}

	@Override
	public @NonNull UserSectionProgress upsertStatus(@NonNull Long userId,
	                                                 @NonNull Integer sectionId,
	                                                 @NonNull ProgressStatus status) {
		UserSectionProgress progress = repository.findById(new UserSectionProgressId(userId, sectionId))
				.orElseGet(UserSectionProgress::new);
		progress.setUserId(userId);
		progress.setSectionId(sectionId);
		progress.setStatus(status);
		return repository.save(progress);
	}
}
