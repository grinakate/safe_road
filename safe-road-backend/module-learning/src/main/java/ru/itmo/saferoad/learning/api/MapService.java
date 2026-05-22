package ru.itmo.saferoad.learning.api;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.api.ContentService;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserTopicProgress;
import ru.itmo.saferoad.learning.dto.TopicUserMapResponse;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class MapService {

	private final ContentService contentService;
	private final UserTopicProgressService userTopicProgressService;

	@NonNull
	public List<UserSectionResponse> getMapForUser(@NonNull Long userId) {
		List<SectionTreeDto> structure = contentService.getSectionTree();

		Map<Integer, ProgressStatus> progress = userTopicProgressService.getByUserId(userId)
				.stream()
				.collect(Collectors.toMap(UserTopicProgress::getTopicId, UserTopicProgress::getStatus));

		return structure.stream().map(s -> mapSection(s, progress)).toList();
	}

	/**
	 * Превращает статическую секцию в динамическую с учетом прогресса
	 */
	private UserSectionResponse mapSection(SectionTreeDto section,
										   Map<Integer, ProgressStatus> progressMap) {
		// Маппим список топиков
		List<TopicUserMapResponse> topicResponses = section.getTopics().stream()
				.map(topic -> TopicUserMapResponse.builder()
						.id(topic.getId())
						.title(topic.getTitle())
						.orderIndex(topic.getOrderIndex())
						// Если записи в БД нет, по умолчанию топик заблокирован (LOCKED)
						.status(progressMap.getOrDefault(topic.getId(), ProgressStatus.LOCKED))
						.build())
				.toList();

		// Рассчитываем прогресс секции на основе обработанных топиков
		int progressPercent = calculatePercent(topicResponses);

		return UserSectionResponse.builder()
				.id(section.getId())
				.title(section.getTitle())
				.progressPercent(progressPercent)
				.topics(topicResponses)
				.build();
	}

	/**
	 * Рассчитывает процент завершенных топиков в секции
	 */
	private int calculatePercent(List<TopicUserMapResponse> topics) {
		if (topics == null || topics.isEmpty()) {
			return 0;
		}

		// Считаем количество топиков со статусом COMPLETED
		long completedCount = topics.stream()
				.filter(t -> t.getStatus() == ProgressStatus.COMPLETED)
				.count();

		// Вычисляем процент: (завершенные / всего) * 100
		double percent = (double) completedCount / topics.size() * 100;

		return (int) Math.round(percent);
	}
}
