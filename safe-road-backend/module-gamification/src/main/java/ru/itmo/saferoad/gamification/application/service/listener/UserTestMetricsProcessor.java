package ru.itmo.saferoad.gamification.application.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.gamification.domain.UserMetricCode;
import ru.itmo.saferoad.gamification.domain.event.TestCompletedDomainEvent;
import ru.itmo.saferoad.gamification.service.UserMetricService;

import java.util.Objects;

/**
 * Обработчик доменного события TestCompletedDomainEvent.
 * Отвечает за обновление детальных метрик пользователя (идеальные тесты, серии правильных ответов и т.д.).
 * <p>
 * Порядок выполнения: @Order(3) - ТРЕТИЙ слушатель
 * Выполняется в конце, после обновления серии дней и расчета XP.
 * <p>
 * Single Responsibility: отслеживание детальных статистик пользователя.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserTestMetricsProcessor {

	private final UserMetricService userMetricService;

	@Order(3)
	@EventListener
	@Transactional
	public void onTestCompleted(TestCompletedDomainEvent event) {
		// Количество 100% правильных тестов подряд:
		var payload = event.getPayload();

		if (!Objects.equals(payload.getCorrectAnswers(), payload.getTotalQuestions())) {
			return;
		}
		userMetricService.increaseUserMetric(event.getUserId(), 1, UserMetricCode.PERFECT_TEST_STREAK);
	}
}


