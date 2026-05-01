package ru.itmo.saferoad.content.service;

import lombok.NonNull;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.QuestionType;

import java.util.List;
import java.util.Map;

public interface QuestionService {

	@NonNull
	List<Question> getByTopicId(@NonNull Integer topicId);

	@NonNull
	Question create(@NonNull Integer topicId,
	                @NonNull QuestionType type,
	                @NonNull Integer difficultyLevel,
	                @NonNull Map<String, Object> content);

	long countByTopicId(@NonNull Integer topicId);

	@NonNull
	Question existingById(@NonNull Long id);
}
