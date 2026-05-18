package ru.itmo.saferoad.learning.application.scheduler;

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
public class LearningPendingEventsScheduler {

	private static final List<EventType> LEARNING_EVENT_TYPES = List.of(
			EventType.OPEN_FIRST_TOPIC
	);

	private final ExecutorService executorService;
	private final EventExecutor eventExecutor;
	private final LearningPendingEventsProperties properties;
	private final PendingEventsRepository pendingEventsRepository;

	public LearningPendingEventsScheduler(PendingEventsRepository pendingEventsRepository,
										  EventExecutor eventExecutor,
										  LearningPendingEventsProperties properties) {
		this.pendingEventsRepository = pendingEventsRepository;
		this.eventExecutor = eventExecutor;
		this.executorService = Executors.newFixedThreadPool(properties.getScheduledTreadCount());
		this.properties = properties;
	}

	@Scheduled(fixedDelayString = "${learning.events.scheduler-poll-interval-ms:5000}")
	public void processPendingEvents() {
		log.info("Running scheduled check for pending learning events...");

		List<PendingEvent> events = pendingEventsRepository.findByStatusAndTypeIn(
				ProgressStatus.PENDING, LEARNING_EVENT_TYPES, Pageable.ofSize(properties.getScheduledTreadCount())
		);

		if (events.isEmpty()) {
			log.debug("No learning pending events found");
			return;
		}

		log.info("Found {} learning pending events to process", events.size());

		for (PendingEvent event : events) {
			executorService.submit(() -> {
				eventExecutor.process(event);
			});
		}
	}
}
