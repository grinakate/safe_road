package ru.itmo.saferoad.core.event;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.core.domain.repository.PendingEventsRepository;

@Slf4j
@Service
@AllArgsConstructor
public class EventExecutor {

	private final PendingEventsRepository pendingEventsRepository;
	private final PendingEventProcessorsRegister pendingEventProcessorsRegister;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void process(@NonNull PendingEvent event) {
		log.info("Starting processing event ID: {}", event.getId());
		try {
			pendingEventsRepository.updateStatus(event.getId(), ProgressStatus.IN_PROGRESS);
			var processor = pendingEventProcessorsRegister.getEventProcessor(event.getType());
			processor.processEvent(event);
			pendingEventsRepository.updateStatus(event.getId(), ProgressStatus.COMPLETED);
			log.info("Successfully processed event ID: {}", event.getId());
		} catch (Exception e) {
			log.error("Failed to process event ID: {}", event.getId(), e);
			pendingEventsRepository.updateStatus(event.getId(), ProgressStatus.FAILED);
		}
	}
}

