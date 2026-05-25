package ru.itmo.saferoad.learning.service.impl;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.learning.domain.ReviewInterval;
import ru.itmo.saferoad.learning.domain.repository.ReviewIntervalRepository;

import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewSchedulerTest {

    @Mock
    private ReviewIntervalRepository repo;

    @InjectMocks
    private ReviewScheduler scheduler;

    @Test
    void getHoursForStreak_correctUsesStreakOtherwiseUsesZero() {
        // Given
        Integer streakLevel = 1;
        Integer hoursForStreak = 48;
        ReviewInterval intervalForStreak = Instancio.of(ReviewInterval.class)
                .set(field(ReviewInterval::getStreakLevel), streakLevel)
                .set(field(ReviewInterval::getIntervalHours), hoursForStreak)
                .create();
        when(repo.findById(streakLevel)).thenReturn(Optional.of(intervalForStreak));

        Integer wrongStreak = 5;
        Integer hoursForWrong = 2;
        ReviewInterval intervalForZero = Instancio.of(ReviewInterval.class)
                .set(field(ReviewInterval::getStreakLevel), 0)
                .set(field(ReviewInterval::getIntervalHours), hoursForWrong)
                .create();
        when(repo.findById(0)).thenReturn(Optional.of(intervalForZero));

        // When
        int resultForStreak = scheduler.getHoursForStreak(streakLevel, true);
        int resultForWrong = scheduler.getHoursForStreak(wrongStreak, false);

        // Then
        assertEquals(hoursForStreak.intValue(), resultForStreak);
        assertEquals(hoursForWrong.intValue(), resultForWrong);
    }
}

