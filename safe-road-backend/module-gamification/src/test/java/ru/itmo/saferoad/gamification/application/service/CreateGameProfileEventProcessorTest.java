package ru.itmo.saferoad.gamification.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.event.dto.CreateGameProfileEvent;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.gamification.config.GamificationProperties;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import org.instancio.Instancio;
import static org.instancio.Select.field;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGameProfileEventProcessorTest {

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private GameProfileService gameProfileService;

    @Mock
    private GamificationProperties gamificationProperties;

    @Mock
    private CurrentTime currentTime;

    @InjectMocks
    private CreateGameProfileEventProcessor processor;

    @Test
    void getEventType_returnsCreateGameProfile() {
        // Then
        assert processor.getEventType().name().equals("CREATE_GAME_PROFILE");
    }

    @Test
    void processEvent_shouldCreateProfileWithLeaderboardFlag() {
        // Given
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        CreateGameProfileEvent payload = Instancio.of(CreateGameProfileEvent.class)
                .set(field(CreateGameProfileEvent::getBirthDate), birthDate)
                .create();

        PendingEvent event = Instancio.of(PendingEvent.class)
                .set(field(PendingEvent::getContent), "irrelevant")
                .create();

        when(jsonMapper.readValue(anyString(), eq(CreateGameProfileEvent.class))).thenReturn(payload);
        LocalDate nowDate = LocalDate.of(2025, 1, 1);
        when(currentTime.nowDate()).thenReturn(nowDate);
        when(gamificationProperties.getMinAgeForDefaultLeaderBoarder()).thenReturn(18);

        // When
        processor.processEvent(event);

        // Then
        verify(gameProfileService, times(1)).create(payload.getUserId(), false);
    }
}

