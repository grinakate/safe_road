package ru.itmo.saferoad.learning.service;

import ru.itmo.saferoad.learning.dto.StartTestRequest;
import ru.itmo.saferoad.learning.dto.StartTestResponse;
import ru.itmo.saferoad.learning.dto.SubmitSessionAnswerRequest;
import ru.itmo.saferoad.learning.dto.TestQuestionResponse;
import ru.itmo.saferoad.learning.dto.TestResultResponse;
import ru.itmo.saferoad.learning.dto.TestSessionSubmitResponse;

import java.util.List;

public interface TestSessionService {
    StartTestResponse startTopicTest(StartTestRequest request, Long userId);
    List<TestQuestionResponse> getTestQuestions(Integer sessionId, Long userId);
    TestSessionSubmitResponse submitSessionAnswer(Integer sessionId, SubmitSessionAnswerRequest request, Long userId);
    TestResultResponse getTestResult(Integer sessionId, Long userId);
}
