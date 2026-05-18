package ru.itmo.saferoad.learning.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.service.SectionService;
import ru.itmo.saferoad.learning.dto.QuizResultResponse;
import ru.itmo.saferoad.learning.dto.SectionStatResponse;
import ru.itmo.saferoad.learning.dto.UserAnswerRequest;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LearningService {

	private final SectionService sectionService;
	private final UserTopicProgressService userTopicProgressService;

	public List<SectionStatResponse> getUserStats(@NonNull Long userId) {
		// 1. Получаем все секции (можно использовать кэш Caffeine здесь!)
		List<Section> sections = sectionService.getAllOrdered();

		return sections.stream().map(section -> {
			// 2. Считаем общее количество топиков в секции
			int totalTopics = section.getTopics().size();

			if (totalTopics == 0) {
				return new SectionStatResponse(section.getTitle(), 0);
			}

			// 3. Считаем сколько из них пройдено пользователем
			long completedCount = userTopicProgressService.countCompletedTopicsInSection(userId, section.getId());

			// 4. Вычисляем процент
			int percent = (int) (((double) completedCount / totalTopics) * 100);

			return new SectionStatResponse(section.getTitle(), percent);
		}).toList();
	}

	@NotNull
	public QuizResultResponse getQuizResult(@NotNull List<UserAnswerRequest> userAnswers) {
		/*Map<Integer, Long> questionByAnswerIds = userAnswers.stream()
				.collect(Collectors.toMap(
						UserAnswerRequest::getSelectedAnswerId,
						UserAnswerRequest::getQuestionId)
				);

		List<Answer> answers = answerService.existingByIds(questionByAnswerIds.keySet());

		int correctAnswerCount = Math.toIntExact(answers.stream()
				.filter(answer -> questionByAnswerIds.get(answer.getId()).equals(answer.getQuestion().getId())
								  && answer.getIsCorrect())
				.count());

		int xpResult = 1 + answers.stream()
				.filter(answer -> questionByAnswerIds.get(answer.getId()).equals(answer.getQuestion().getId())
								  && answer.getIsCorrect())
				.map(answer -> {
					var question = answer.getQuestion();
					return question.getDifficultyLevel() * 2;
				})
				.mapToInt(Integer::intValue).sum();

		return new QuizResultResponse(correctAnswerCount, userAnswers.size(), xpResult, false);*/
		return new QuizResultResponse(0, userAnswers.size(), 0, false);
	}
}
