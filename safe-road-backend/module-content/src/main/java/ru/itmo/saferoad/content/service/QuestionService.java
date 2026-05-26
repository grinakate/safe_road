package ru.itmo.saferoad.content.service;

import lombok.NonNull;
import ru.itmo.saferoad.content.domain.Question;

public interface QuestionService {

	@NonNull
	Question existingById(@NonNull Long id);

	Question save(Question question);

}
