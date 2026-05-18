package ru.itmo.saferoad.gamification.application.scheduler;

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
public class GamificationPendingEventsScheduler {

	private static final List<EventType> GAMIFICATION_EVENT_TYPES = List.of(
			EventType.USER_REGISTERED,
			EventType.TEST_SESSIONS_COMPLETED
	);

	private final ExecutorService executorService;
	private final EventExecutor eventExecutor;
	private final GamificationPendingEventsProperties properties;
	private final PendingEventsRepository pendingEventsRepository;

	public GamificationPendingEventsScheduler(PendingEventsRepository pendingEventsRepository,
											  EventExecutor eventExecutor,
											  GamificationPendingEventsProperties properties) {
		this.pendingEventsRepository = pendingEventsRepository;
		this.eventExecutor = eventExecutor;
		this.executorService = Executors.newFixedThreadPool(properties.getScheduledTreadCount());
		this.properties = properties;
	}

	@Scheduled(fixedDelay = 5000)
	public void processPendingEvents() {
		log.info("Running scheduled check for pending gamification events...");

		List<PendingEvent> events = pendingEventsRepository.findByStatusAndTypeIn(
				ProgressStatus.PENDING, GAMIFICATION_EVENT_TYPES, Pageable.ofSize(properties.getScheduledTreadCount())
		);

		if (events.isEmpty()) {
			log.debug("No USER_REGISTERED pending events found");
			return;
		}

		log.info("Found {} USER_REGISTERED pending events to process", events.size());

		for (PendingEvent event : events) {
			executorService.submit(() -> {
				eventExecutor.process(event);
			});
		}
	}
}
