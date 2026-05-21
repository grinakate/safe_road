package ru.itmo.saferoad.learning.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
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

	@Override
	public void unlockTopic(@NonNull Long userId, @NonNull Topic topic) {
		var userTopicProgress = new UserTopicProgress();
		userTopicProgress.setUserId(userId);
		userTopicProgress.setTopicId(topic.getId());
		userTopicProgress.setStatus(ProgressStatus.UNLOCKED);
		repository.save(userTopicProgress);
	}

	@Override
	public void updateProgress(@NonNull Long userId, @NonNull Integer topicId, @NonNull ProgressStatus status) {
		var userTopicProgress = repository.findById(new UserTopicProgressId(userId, topicId)).orElseThrow();
		userTopicProgress.setStatus(status);
	}
}
