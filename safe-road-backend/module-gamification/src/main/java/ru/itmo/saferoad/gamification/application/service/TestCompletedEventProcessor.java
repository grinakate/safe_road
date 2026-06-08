package ru.itmo.saferoad.gamification.application.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.event.EventProcessor;
import ru.itmo.saferoad.core.event.dto.TestSessionCompletedEvent;
import ru.itmo.saferoad.gamification.domain.event.TestCompletedDomainEvent;
import tools.jackson.databind.json.JsonMapper;

/**
 * Оркестратор для обработки события завершения теста.
 * <p>
 * Отвечает только за:
 * 1. Десериализацию PendingEvent в TestSessionCompletedEvent
 * 2. Публикацию доменного события TestCompletedDomainEvent
 * 3. Вызов AchievementOrchestrator для проверки достижений
 * 4. Обработку исключений
 * <p>
 * Все остальные операции (расчет XP, обновление уровня, streak и т.д.)
 * делегированы именованным обработчикам через Spring Application Events.
 * <p>
 * Single Responsibility: координация обработки события.
 * Open/Closed: легко добавлять новых слушателей без изменения этого класса.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestCompletedEventProcessor implements EventProcessor {

	private final JsonMapper jsonMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final AchievementOrchestrator achievementOrchestrator;

	@Override
	public EventType getEventType() {
		return EventType.TEST_SESSIONS_COMPLETED;
	}

	@Override
	@Transactional
	public void processEvent(@NonNull PendingEvent event) {
		// Этап 1: Десериализация события
		TestSessionCompletedEvent payload = jsonMapper.readValue(
				event.getContent(),
				TestSessionCompletedEvent.class
		);
		Long userId = payload.getUserId();

		// Этап 2: Публикация доменного события для расчета метрик:
		TestCompletedDomainEvent domainEvent = new TestCompletedDomainEvent(this, userId, payload);
		eventPublisher.publishEvent(domainEvent);

		// Этап 3: Проверка и обработка достижений
		achievementOrchestrator.processTriggers(userId);
	}
}
