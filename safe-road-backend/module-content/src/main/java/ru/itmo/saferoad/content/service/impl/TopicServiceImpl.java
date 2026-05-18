package ru.itmo.saferoad.content.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.content.domain.repository.TopicRepository;
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.content.service.TopicService;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

	private final TopicRepository topicRepository;
	private final SectionService sectionService;

	@Override
	public @NonNull List<Topic> getBySectionId(@NonNull Integer sectionId) {
		return topicRepository.findBySectionId(sectionId).stream()
				.sorted(Comparator.comparing(Topic::getOrderIndex))
				.toList();
	}

	@Override
	public @NonNull List<Topic> getAllOrdered() {
		return topicRepository.findAll().stream()
				.sorted(Comparator.comparing(Topic::getOrderIndex))
				.toList();
	}

	@Override
	public @NonNull Topic create(@NonNull Integer sectionId,
								 @NonNull String name,
								 String description,
								 @NonNull Integer orderIndex,
								 @NonNull Integer xpReward) {
		Topic topic = new Topic();
		topic.setSection(sectionService.existingById(sectionId));
		topic.setTitle(name);
		topic.setContent(description);
		topic.setOrderIndex(orderIndex);
		topic.setIsActive(true);
		return topicRepository.save(topic);
	}

	@Override
	public @NonNull Topic existingById(@NonNull Integer id) {
		return topicRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Topic with id " + id + " does not exist"));
	}
}
