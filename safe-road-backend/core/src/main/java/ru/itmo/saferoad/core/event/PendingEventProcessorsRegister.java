package ru.itmo.saferoad.core.event;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.core.domain.enums.EventType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PendingEventProcessorsRegister {

	private final Map<EventType, EventProcessor> eventProcessorsByType;

	public PendingEventProcessorsRegister(List<EventProcessor> eventProcessors) {
		eventProcessorsByType = eventProcessors.stream()
				.collect(Collectors.toMap(EventProcessor::getEventType, Function.identity()));
	}

	@NonNull
	public EventProcessor getEventProcessor(@NonNull EventType eventType) {
		var processor = eventProcessorsByType.getOrDefault(eventType, null);
		if (processor == null) {
			throw new IllegalStateException("No EventProcessor registered for type " + eventType);
		}
		return processor;
	}
}
