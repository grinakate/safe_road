package ru.itmo.saferoad.learning.service.impl;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.api.ContentService;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.learning.api.SectionMapper;
import ru.itmo.saferoad.learning.domain.UserQuestionStats;
import ru.itmo.saferoad.learning.domain.repository.UserQuestionStatsRepository;
import ru.itmo.saferoad.learning.dto.SectionStatisticsResponse;
import ru.itmo.saferoad.learning.dto.TopicStatResponse;
import ru.itmo.saferoad.learning.dto.TopicUserMapResponse;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StatisticsService {

	private final SectionMapper sectionMapper;
	private final ContentService contentService;
	private final UserTopicProgressService userTopicProgressService;
	private final QuestionRepository questionRepository;
	private final UserQuestionStatsRepository userQuestionStatsRepository;

	@NotNull
	public List<SectionStatisticsResponse> getStatisticsForUser(@NotNull Long userId) {
		List<UserSectionResponse> sections = contentService.getSectionTree().stream()
				.map(sectionMapper::toUserSectionResponse)
				.toList();

		return sections.stream()
				.map(section -> buildSectionStats(section, userId))
				.toList();
	}

	SectionStatisticsResponse buildSectionStats(UserSectionResponse section, Long userId) {
		var topicStats = section.getTopics().stream()
				.map(topic -> computeTopicStat(topic, userId))
				.toList();

		int completed = userTopicProgressService.countCompletedTopicsInSection(userId, section.getId());
		int totalTopics = section.getTopics() == null ? 0 : section.getTopics().size();

		return SectionStatisticsResponse.builder()
				.sectionId(section.getId())
				.title(section.getTitle())
				.completedTopics(completed)
				.totalTopics(totalTopics)
				.topicStatistics(topicStats)
				.build();
	}

	TopicStatResponse computeTopicStat(TopicUserMapResponse topic, Long userId) {
		int topicId = topic.getId();
		var questions = questionRepository.findByTopicId(topicId);
		int totalQuestions = questions.size();

		var userStats = userQuestionStatsRepository.findByUserIdAndTopicId(userId, topicId);
		int seen = userStats.size();
		int correct = (int) userStats.stream().filter(UserQuestionStats::getLastResultCorrect).count();
		int wrong = seen - correct;
		int notShown = Math.max(0, totalQuestions - seen);

		return TopicStatResponse.builder()
				.topicId(topic.getId())
				.title(topic.getTitle())
				.totalQuestions(totalQuestions)
				.correctAnswers(correct)
				.wrongAnswers(wrong)
				.notShown(notShown)
				.build();
	}

}
