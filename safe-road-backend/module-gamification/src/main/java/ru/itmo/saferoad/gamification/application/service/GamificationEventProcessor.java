package ru.itmo.saferoad.gamification.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.domain.PendingEvents;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;

@Service
@Slf4j
public class GamificationEventProcessor {

    private final PendingEventsRepository pendingEventsRepository;

    public GamificationEventProcessor(PendingEventsRepository pendingEventsRepository) {
        this.pendingEventsRepository = pendingEventsRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processEvent(PendingEvents event) {
        log.info("Starting processing event ID: {}", event.getId());
        
        try {
            pendingEventsRepository.updateStatus(event.getId(), ProgressStatus.IN_PROGRESS);

            // TODO: Выполнить логику обработки события Achievements / Levels и тд
            // Например:
            // if (event.getType() == EventType.GAMIFICATION_ACHIEVEMENT_CHECK) {
            //      achievementCheckerService.check(event.getContent());
            // }

            pendingEventsRepository.updateStatus(event.getId(), ProgressStatus.COMPLETED);
            log.info("Successfully processed event ID: {}", event.getId());
        } catch (Exception e) {
            log.error("Failed to process event ID: {}", event.getId(), e);
            pendingEventsRepository.updateStatus(event.getId(), ProgressStatus.FAILED);
        }
    }
}

