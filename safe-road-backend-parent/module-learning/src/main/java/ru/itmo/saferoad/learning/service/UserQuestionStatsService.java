package ru.itmo.saferoad.learning.service;

import com.example.learning.dto.SubmitAnswerResponse;
import lombok.NonNull;

public interface UserQuestionStatsService {

	@NonNull
	SubmitAnswerResponse submitAnswer(@NonNull Long userId, @NonNull Long questionId, @NonNull Integer answerId);
}
