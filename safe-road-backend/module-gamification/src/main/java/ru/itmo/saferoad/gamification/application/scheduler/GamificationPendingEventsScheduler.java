package ru.itmo.saferoad.gamification.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.core.domain.PendingEvents;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;
import ru.itmo.saferoad.gamification.application.service.GamificationEventProcessor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class GamificationPendingEventsScheduler {

    private final PendingEventsRepository pendingEventsRepository;
    private final GamificationEventProcessor eventProcessor;
    private final ExecutorService executorService;

    public GamificationPendingEventsScheduler(PendingEventsRepository pendingEventsRepository, 
                                              GamificationEventProcessor eventProcessor) {
        this.pendingEventsRepository = pendingEventsRepository;
        this.eventProcessor = eventProcessor;
        // Пуул потоков для параллельного выполнения обработки ивентов
        this.executorService = Executors.newFixedThreadPool(10);
    }

    @Scheduled(fixedDelay = 15000)
    public void processPendingEvents() {
        log.info("Running scheduled check for pending gamification events...");
        
        List<PendingEvents> events = pendingEventsRepository.findByStatusAndType(
                ProgressStatus.PENDING, EventType.GAMIFICATION_ACHIEVEMENT_CHECK
        );

        if (events.isEmpty()) {
            log.debug("No pending events found");
            return;
        }

        log.info("Found {} pending events to process", events.size());

        for (PendingEvents event : events) {
            executorService.submit(() -> {
                eventProcessor.processEvent(event);
            });
        }
    }
}
