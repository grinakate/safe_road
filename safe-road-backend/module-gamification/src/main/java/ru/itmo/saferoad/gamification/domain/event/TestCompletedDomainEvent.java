package ru.itmo.saferoad.gamification.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;

/**
 * Доменное событие, опубликованное когда тест завершён.
 * Используется для обработки различных аспектов (XP, уровни, streak, метрики).
 */
@Getter
public class TestCompletedDomainEvent extends ApplicationEvent {

	private final Long userId;
	private final TestSessionCompletedEvent payload;

	public TestCompletedDomainEvent(Object source, Long userId, TestSessionCompletedEvent payload) {
		super(source);
		this.userId = userId;
		this.payload = payload;
	}
}


