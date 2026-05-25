package ru.itmo.saferoad.gamification.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;
import ru.itmo.saferoad.core.event.dto.NotificationEvent;
import ru.itmo.saferoad.core.event.CreatePendingEventsService;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.Level;
import ru.itmo.saferoad.gamification.domain.repository.GameProfileRepository;
import ru.itmo.saferoad.gamification.domain.repository.LevelRepository;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import ru.itmo.saferoad.gamification.service.UserMetricService;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;

import ru.itmo.saferoad.gamification.config.GamificationProperties;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.instancio.Instancio;
import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
class TestCompletedEventProcessorTest {

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private GameProfileService gameProfileService;

    @Mock
    private GameProfileRepository gameProfileRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private UserMetricService userMetricService;

    @Mock
    private CreatePendingEventsService createPendingEventsService;

    @Mock
    private GamificationProperties gamificationProperties;

    @InjectMocks
    private TestCompletedEventProcessor processor;

    @Test
    void getEventType_returnsTestSessionsCompleted() {
        assertEquals("TEST_SESSIONS_COMPLETED", processor.getEventType().name());
    }

    @Test
    void processEvent_withEarnedXp_shouldUpdateProfileAndPublishNotification() {
        // Given
        // Use variables and Instancio to avoid hardcoded object construction
        Long userId = Instancio.create(Long.class);
        Integer baseXp = Instancio.create(Integer.class);
        String qType = Instancio.create(String.class);

        var d = Instancio.of(TestSessionCompletedEvent.QuestionDetail.class)
                .set(field(TestSessionCompletedEvent.QuestionDetail::getType), qType)
                .set(field(TestSessionCompletedEvent.QuestionDetail::getDifficulty), 1)
                .set(field(TestSessionCompletedEvent.QuestionDetail::getIsCorrect), true)
                .create();

        TestSessionCompletedEvent payload = Instancio.of(TestSessionCompletedEvent.class)
                .set(field(TestSessionCompletedEvent::getUserId), userId)
                .set(field(TestSessionCompletedEvent::getDetails), List.of(d))
                .create();

        PendingEvent event = Instancio.of(PendingEvent.class)
                .set(field(PendingEvent::getContent), "payload")
                .create();
        when(jsonMapper.readValue(anyString(), eq(TestSessionCompletedEvent.class))).thenReturn(payload);
        Level currentLevel = Instancio.of(Level.class)
                .set(field(Level::getNumber), 1)
                .set(field(Level::getXpThreshold), 100)
                .create();

        GameProfile profile = Instancio.of(GameProfile.class)
                .set(field(GameProfile::getUserId), userId)
                .set(field(GameProfile::getLevel), currentLevel)
                .set(field(GameProfile::getXp), 0)
                .create();

        when(gameProfileService.existingByUserId(userId)).thenReturn(profile);

        when(gamificationProperties.getBaseXpPerQuestion()).thenReturn(baseXp);
        when(gamificationProperties.getQuestionTypeCoefficients()).thenReturn(java.util.Map.of(qType, 1.0));

        Level nextLevel = Instancio.of(Level.class)
                .set(field(Level::getNumber), 2)
                .set(field(Level::getXpThreshold), 200)
                .create();
        when(levelRepository.findByNumber(2)).thenReturn(Optional.of(nextLevel));

        // When
        processor.processEvent(event);

        // Then
        verify(gameProfileRepository, times(1)).save(profile);
        verify(userMetricService, times(1)).addXp(userId, baseXp);
        verify(createPendingEventsService, times(1)).publishCreateNotificationEvent(any(NotificationEvent.class));
    }
}





