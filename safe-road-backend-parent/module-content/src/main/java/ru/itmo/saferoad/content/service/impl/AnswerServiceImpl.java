package ru.itmo.saferoad.content.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Answer;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.content.domain.repository.AnswerRepository;
import ru.itmo.saferoad.content.service.AnswerService;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

	private final AnswerRepository answerRepository;

	@Override
	public @NonNull List<Answer> getByQuestionId(@NonNull Long questionId) {
		return answerRepository.findByQuestionId(questionId);
	}

	@Override
	public @NonNull Answer create(@NonNull Question question,
								  @NonNull String text,
								  @NonNull Boolean isCorrect,
								  @NonNull String feedback) {
		Answer answer = new Answer();
		answer.setQuestion(question);
		answer.setText(text);
		answer.setIsCorrect(isCorrect);
		answer.setFeedback(feedback);
		return answerRepository.save(answer);
	}

	@Override
	public @NonNull Answer existingById(@NonNull Integer id) {
		return answerRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Answer with id " + id + " does not exist"));
	}

	@Override
	public @NonNull List<Answer> existingByIds(@NonNull Set<Integer> ids) {
		var result = answerRepository.findAllById(ids);
		if (ids.size() != result.size()) {
			throw new IllegalArgumentException("Answer with id " + ids + " does not exist");
		}
		return result;
	}
}
