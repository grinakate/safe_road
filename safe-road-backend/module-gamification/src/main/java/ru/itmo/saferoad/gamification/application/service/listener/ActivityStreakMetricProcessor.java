package ru.itmo.saferoad.gamification.application.service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.saferoad.core.time.CurrentTime;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.event.TestCompletedDomainEvent;
import ru.itmo.saferoad.gamification.domain.repository.GameProfileRepository;
import ru.itmo.saferoad.gamification.service.GameProfileService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Обработчик доменного события TestCompletedDomainEvent.
 * Отвечает за обновление серии дней активности (streak).
 * <p>
 * Порядок выполнения: @Order(1) - ПЕРВЫЙ слушатель
 * Гарантирует, что серия дней будет обновлена ДО выполнения других слушателей.
 * <p>
 * Single Responsibility: отслеживание streak пользователя.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ActivityStreakMetricProcessor {

	private final GameProfileService gameProfileService;
	private final GameProfileRepository gameProfileRepository;
	private final CurrentTime currentTime;

	@Order(1)
	@EventListener
	@Transactional
	public void onTestCompleted(TestCompletedDomainEvent event) {
		Long userId = event.getUserId();
		GameProfile profile = gameProfileService.existingByUserId(userId);

		LocalDate today = currentTime.nowDateTime().toLocalDate();
		LocalDateTime lastActivity = profile.getLastActivityDate();

		if (lastActivity == null) {
			// Первая активность - начинаем streak с 1
			profile.setCurrentStreak(1);
			profile.setTotalActiveDays(1);
			profile.setLastActivityDate(currentTime.nowDateTime());
		} else {
			LocalDate lastActivityDate = lastActivity.toLocalDate();

			if (lastActivityDate.equals(today)) {
				// Активность уже была сегодня, не обновляем streak
				return;
			}

			long daysBetween = ChronoUnit.DAYS.between(lastActivityDate, today);

			if (daysBetween == 1) {
				// Активность вчера - продолжаем streak
				profile.setCurrentStreak(profile.getCurrentStreak() + 1);
				profile.setTotalActiveDays(profile.getTotalActiveDays() + 1);
			} else if (daysBetween > 1) {
				// Разрыв streak - начинаем заново
				profile.setCurrentStreak(1);
				profile.setTotalActiveDays(profile.getTotalActiveDays() + 1);
			}

			profile.setLastActivityDate(currentTime.nowDateTime());
		}

		gameProfileRepository.save(profile);
	}
}




