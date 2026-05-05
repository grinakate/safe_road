package ru.itmo.saferoad.content.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionType;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.content.service.QuestionService;
import ru.itmo.saferoad.content.service.TopicService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

	private final QuestionRepository questionRepository;
	private final TopicService topicService;

	@Override
	public @NonNull List<Question> getByTopicId(@NonNull Integer topicId) {
		return questionRepository.findByTopicId(topicId);
	}

	@Override
	public @NonNull Question create(@NonNull Integer topicId,
									@NonNull QuestionType type,
									@NonNull Integer difficultyLevel,
									@NonNull Map<String, Object> content) {
		Question question = new Question();
		question.setTopic(topicService.existingById(topicId));
		question.setType(type);
		question.setDifficultyLevel(difficultyLevel);
		//question.setContent(content);
		return questionRepository.save(question);
	}

	@Override
	public long countByTopicId(@NonNull Integer topicId) {
		return questionRepository.countByTopicId(topicId);
	}

	@Override
	public @NonNull Question existingById(@NonNull Long id) {
		return questionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Question with id " + id + " does not exist"));
	}
}
