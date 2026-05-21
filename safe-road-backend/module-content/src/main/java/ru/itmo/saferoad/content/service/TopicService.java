package ru.itmo.saferoad.content.service;

import lombok.NonNull;
import ru.itmo.saferoad.content.domain.Topic;

import java.util.List;
import java.util.Optional;

public interface TopicService {

	@NonNull
	List<Topic> getBySectionId(@NonNull Integer sectionId);

	@NonNull
	List<Topic> getAllOrdered();

	@NonNull
	Topic create(@NonNull Integer sectionId,
	             @NonNull String name,
	             String description,
	             @NonNull Integer orderIndex,
	             @NonNull Integer xpReward);

	@NonNull
	Topic existingById(@NonNull Integer id);

	Optional<Topic> getNextTopic(@NonNull Topic currentTopic);
}
