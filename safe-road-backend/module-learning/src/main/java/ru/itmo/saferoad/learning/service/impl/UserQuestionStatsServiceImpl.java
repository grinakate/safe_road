package ru.itmo.saferoad.learning.service.impl;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.learning.dto.SubmitAnswerResponse;
import ru.itmo.saferoad.learning.service.UserQuestionStatsService;

@Service
public class UserQuestionStatsServiceImpl implements UserQuestionStatsService {
	@Override
	public @NonNull SubmitAnswerResponse submitAnswer(@NonNull Long userId, @NonNull Long questionId, @NonNull Integer answerId) {
		return null;
	}
}
