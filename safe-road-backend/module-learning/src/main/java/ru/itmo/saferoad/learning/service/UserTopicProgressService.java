package ru.itmo.saferoad.learning.service;

import lombok.NonNull;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;

import java.util.List;
import java.util.Optional;

public interface UserTopicProgressService {

	@NonNull
	List<UserTopicProgress> getByUserId(@NonNull Long userId);

	@NonNull
	Optional<UserTopicProgress> getByUserIdAndTopicId(@NonNull Long userId, @NonNull Integer topicId);

	@NonNull
	UserTopicProgress upsertStatus(@NonNull Long userId, @NonNull Integer topicId, @NonNull ProgressStatus status);

	int countCompletedTopicsInSection(@NonNull Long userId, @NonNull Integer id);

	void unlockTopic(@NonNull Long userId, @NonNull Topic topic);

	void updateProgress(@NonNull Long userId, @NonNull Integer topicId, @NonNull ProgressStatus status);
}
