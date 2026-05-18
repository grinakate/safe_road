package ru.itmo.saferoad.notifications.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;
import ru.itmo.saferoad.core.event.EventExecutor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class NotificationsPendingEventsScheduler {

    private static final List<EventType> NOTIFICATION_EVENT_TYPES = List.of(
            EventType.CREATE_NOTIFICATION
    );

    private final ExecutorService executorService;
    private final EventExecutor eventExecutor;
    private final NotificationsPendingEventsProperties properties;
    private final PendingEventsRepository pendingEventsRepository;

    public NotificationsPendingEventsScheduler(PendingEventsRepository pendingEventsRepository,
                                               EventExecutor eventExecutor,
                                               NotificationsPendingEventsProperties properties) {
        this.pendingEventsRepository = pendingEventsRepository;
        this.eventExecutor = eventExecutor;
        this.executorService = Executors.newFixedThreadPool(properties.getScheduledTreadCount());
        this.properties = properties;
    }

    @Scheduled(fixedDelay = 5000)
    public void processPendingEvents() {
        log.info("Running scheduled check for pending notification events...");

        List<PendingEvent> events = pendingEventsRepository.findByStatusAndTypeIn(
                ProgressStatus.PENDING, NOTIFICATION_EVENT_TYPES, Pageable.ofSize(properties.getScheduledTreadCount())
        );

        if (events.isEmpty()) {
            log.debug("No notification pending events found");
            return;
        }

        log.info("Found {} notification pending events to process", events.size());

        for (PendingEvent event : events) {
            executorService.submit(() -> {
                eventExecutor.process(event);
            });
        }
    }
}

