package ru.itmo.saferoad.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.learning.domain.ReviewInterval;
import ru.itmo.saferoad.learning.domain.repository.ReviewIntervalRepository;

@Component
@RequiredArgsConstructor
public class ReviewScheduler {

    private final ReviewIntervalRepository reviewIntervalRepository;

    /**
     * Returns interval hours for given streak. If the answer was incorrect, returns hours for streak 0.
     * Defaults to 24 hours when not found.
     */
    public int getHoursForStreak(int streak, boolean isCorrect) {
        if (!isCorrect) {
            return reviewIntervalRepository.findById(0)
                    .map(ReviewInterval::getIntervalHours)
                    .orElse(0);
        }
        return reviewIntervalRepository.findById(streak)
                .map(ReviewInterval::getIntervalHours)
                .orElse(24);
    }
}

