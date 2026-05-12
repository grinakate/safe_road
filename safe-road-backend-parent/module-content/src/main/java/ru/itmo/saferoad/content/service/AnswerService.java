package ru.itmo.saferoad.content.service;

import lombok.NonNull;
import ru.itmo.saferoad.content.domain.Answer;
import ru.itmo.saferoad.content.domain.Question;

import java.util.List;
import java.util.Set;

public interface AnswerService {
	@NonNull List<Answer> getByQuestionId(@NonNull Long questionId);

	@NonNull Answer create(@NonNull Question question,
						   @NonNull String text,
						   @NonNull Boolean isCorrect,
						   @NonNull String feedback);

	@NonNull Answer existingById(@NonNull Integer id);

	@NonNull
	List<Answer> existingByIds(@NonNull Set<Integer> ids);
}
