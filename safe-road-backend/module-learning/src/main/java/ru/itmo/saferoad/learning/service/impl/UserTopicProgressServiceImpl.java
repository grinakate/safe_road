package ru.itmo.saferoad.learning.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.core.types.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;
import ru.itmo.saferoad.learning.domain.UserTopicProgressId;
import ru.itmo.saferoad.learning.domain.repository.UserTopicProgressRepository;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTopicProgressServiceImpl implements UserTopicProgressService {

	private final UserTopicProgressRepository repository;

	@Override
	public @NonNull List<UserTopicProgress> getByUserId(@NonNull Long userId) {
		return repository.findByUserId(userId);
	}

	@Override
	public @NonNull UserTopicProgress upsertStatus(@NonNull Long userId,
	                                               @NonNull Integer topicId,
	                                               @NonNull ProgressStatus status) {
		UserTopicProgress progress = repository.findById(new UserTopicProgressId(userId, topicId))
				.orElseGet(UserTopicProgress::new);
		progress.setUserId(userId);
		progress.setTopicId(topicId);
		progress.setStatus(status);
		return repository.save(progress);
	}

	@Override
	public long countCompletedTopicsInSection(@NonNull Long userId, @NonNull Integer sectionId) {
		return repository.countCompletedTopicsInSection(userId, sectionId);
	}
}
