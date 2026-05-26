package ru.itmo.saferoad.content.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.content.service.QuestionService;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

	private final QuestionRepository questionRepository;

	public @NonNull Question existingById(@NonNull Long id) {
		return questionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Question with id " + id + " does not exist"));
	}

	public Question save(Question q) {
		return questionRepository.save(q);
	}
}
