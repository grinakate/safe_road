package ru.itmo.saferoad.learning.service;

import lombok.NonNull;
import ru.itmo.saferoad.core.types.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;

import java.util.List;

public interface UserTopicProgressService {

	@NonNull
	List<UserTopicProgress> getByUserId(@NonNull Long userId);

	@NonNull
	UserTopicProgress upsertStatus(@NonNull Long userId, @NonNull Integer topicId, @NonNull ProgressStatus status);

	long countCompletedTopicsInSection(@NonNull Long userId, @NonNull Integer id);
}
