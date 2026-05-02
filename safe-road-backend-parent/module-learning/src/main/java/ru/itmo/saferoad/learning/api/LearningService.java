package ru.itmo.saferoad.learning.api;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.learning.dto.SectionStatResponse;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;

import java.util.List;

@Service
@AllArgsConstructor
public class LearningService {

	private final SectionService sectionService;
	private final UserTopicProgressService  userTopicProgressService;

	public List<SectionStatResponse> getUserStats(@NonNull Long userId) {
		// 1. Получаем все секции (можно использовать кэш Caffeine здесь!)
		List<Section> sections = sectionService.getAllOrdered();

		return sections.stream().map(section -> {
			// 2. Считаем общее количество топиков в секции
			int totalTopics = section.getTopics().size();

			if (totalTopics == 0) {
				return new SectionStatResponse(section.getName(), 0);
			}

			// 3. Считаем сколько из них пройдено пользователем
			long completedCount = userTopicProgressService.countCompletedTopicsInSection(userId, section.getId());

			// 4. Вычисляем процент
			int percent = (int) (((double) completedCount / totalTopics) * 100);

			return new SectionStatResponse(section.getName(), percent);
		}).toList();
	}
}
