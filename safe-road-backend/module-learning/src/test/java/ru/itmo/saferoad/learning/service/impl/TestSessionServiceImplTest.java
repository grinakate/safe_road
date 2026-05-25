package ru.itmo.saferoad.learning.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Question;
import ru.itmo.saferoad.learning.domain.TestSession;
import ru.itmo.saferoad.learning.dto.StartTestRequest;
import ru.itmo.saferoad.learning.dto.StartTestResponse;
import ru.itmo.saferoad.learning.dto.SubmitSessionAnswerRequest;
import ru.itmo.saferoad.learning.dto.SubmitAnswerResponse;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.learning.domain.repository.TestSessionRepository;
import ru.itmo.saferoad.learning.domain.repository.UserQuestionStatsRepository;
import ru.itmo.saferoad.learning.service.UserQuestionStatsService;
import ru.itmo.saferoad.content.domain.repository.QuestionRepository;
import ru.itmo.saferoad.learning.config.LearningProperties;
import ru.itmo.saferoad.learning.service.UserTopicProgressService;
import ru.itmo.saferoad.content.service.TopicService;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.core.time.CurrentTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
class TestSessionServiceImplTest {

    @Mock
    private TestSessionRepository testSessionRepository;

    @Mock
    private UserQuestionStatsService userQuestionStatsService;

    @Mock
    private CreatePendingEventsService createPendingEventsService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private UserQuestionStatsRepository userQuestionStatsRepository;

    @Mock
    private LearningProperties learningProperties;

    @Mock
    private TopicService topicService;

    @Mock
    private UserTopicProgressService userTopicProgressService;

    @Mock
    private QuestionSelector questionSelector;

    @Mock
    private CurrentTime currentTime;

    @InjectMocks
    private TestSessionServiceImpl service;

    @Test
    void startTest_withTopic_shouldReturnSessionInfo() {
        // Given
        Long userId = Instancio.create(Long.class);
        Integer topicId = Instancio.create(Integer.class);
        StartTestRequest request = Instancio.of(StartTestRequest.class)
                .set(field(StartTestRequest::getTopicId), topicId)
                .create();

        Question q1 = Instancio.of(Question.class).set(field(Question::getId), 101L).create();
        Question q2 = Instancio.of(Question.class).set(field(Question::getId), 102L).create();
        Question q3 = Instancio.of(Question.class).set(field(Question::getId), 103L).create();
        List<Question> allQuestions = List.of(q1, q2, q3);

        when(questionSelector.selectQuestionsAdaptively(anyList(), anyList(), anyInt())).thenReturn(allQuestions);

        when(testSessionRepository.save(any(TestSession.class))).thenAnswer(invocation -> {
            TestSession arg = invocation.getArgument(0);
            arg.setId(999L);
            return arg;
        });

		LocalDateTime now = LocalDateTime.now();
		when(currentTime.nowDateTime()).thenReturn(now);

        // When
        StartTestResponse resp = service.startTest(request, userId);

        // Then
        assertNotNull(resp);
        assertEquals(999L, resp.getSessionId());
        assertEquals(allQuestions.size(), resp.getTotalQuestions());
        verify(testSessionRepository, times(1)).save(any(TestSession.class));
    }

    @Test
    void submitSessionAnswer_allCorrect_shouldSaveAndPublishAndUpdateProgress() {
        // Given
        Integer sessionId = Instancio.create(Integer.class);
        Long userId = Instancio.create(Long.class);

        // create a TestSession with totalQuestions = 5 and topicId not null
        TestSession session = Instancio.of(TestSession.class)
                .set(field(TestSession::getUserId), userId)
                .set(field(TestSession::getTotalQuestions), 5)
                .set(field(TestSession::getCorrectCount), 0)
                .set(field(TestSession::getStatus), ru.itmo.saferoad.core.domain.enums.ProgressStatus.IN_PROGRESS)
                .set(field(TestSession::getTopicId), 11)
                .set(field(TestSession::getQuestionsData), Map.of("questionIds", List.of(201L,202L,203L,204L,205L)))
                .create();

        when(testSessionRepository.findById(eq(sessionId))).thenReturn(Optional.of(session));

        // answers map matching questionIds
        SubmitSessionAnswerRequest request = Instancio.of(SubmitSessionAnswerRequest.class)
                .set(field(SubmitSessionAnswerRequest::getAnswers), Map.of(201L, 1, 202L, 1, 203L, 1, 204L, 1, 205L, 1))
                .create();

        // stub userQuestionStatsService to return correct responses
        when(userQuestionStatsService.submitAnswer(anyLong(), anyLong(), anyInt()))
                .thenReturn(Instancio.of(SubmitAnswerResponse.class).set(field(SubmitAnswerResponse::isCorrect), true).create());

        // stub questionRepository.findById to return minimal Question objects
        when(questionRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long qid = invocation.getArgument(0);
            Question q = Instancio.of(Question.class)
                    .set(field(Question::getId), qid)
                    .set(field(Question::getType), ru.itmo.saferoad.content.domain.QuestionType.CHOICE)
                    .set(field(Question::getDifficultyLevel), 1)
                    .create();
            return Optional.of(q);
        });

        when(currentTime.nowDateTime()).thenReturn(LocalDateTime.now());

        // prepare topic stubbing so updateProgress won't NPE
        Topic topic = Instancio.of(Topic.class).set(field(Topic::getId), 11).create();
        when(topicService.existingById(eq(11))).thenReturn(topic);

        // When
        var resp = service.submitSessionAnswer(sessionId, request, userId);

        // Then
        assertNotNull(resp);
        assertEquals(5, resp.getTotalQuestions());
        assertEquals(5, resp.getCorrectCount());
        verify(testSessionRepository, times(1)).save(session);
        verify(createPendingEventsService, times(1)).publishTestSessionCompletedEvent(any());
        verify(userTopicProgressService, times(1)).updateProgress(eq(userId), anyInt(), any());
    }
}





