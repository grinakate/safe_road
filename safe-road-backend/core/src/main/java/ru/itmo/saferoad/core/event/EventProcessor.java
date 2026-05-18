package ru.itmo.saferoad.core.event;

import lombok.NonNull;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;

public interface EventProcessor {

	@NonNull
	EventType getEventType();

	void processEvent(@NonNull PendingEvent event);
}
