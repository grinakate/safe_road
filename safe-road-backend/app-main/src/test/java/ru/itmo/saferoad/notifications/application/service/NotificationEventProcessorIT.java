package ru.itmo.saferoad.notifications.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.itmo.saferoad.app.SafeRoadApplication;
import ru.itmo.saferoad.core.domain.PendingEvent;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.event.dto.NotificationEvent;
import ru.itmo.saferoad.core.event.dto.notifications.NotificationType;
import ru.itmo.saferoad.notifications.dto.NotificationDto;
import ru.itmo.saferoad.notifications.service.NotificationService;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(classes = SafeRoadApplication.class)
class NotificationEventProcessorIT {

	@Container
	protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
			.withDatabaseName("test")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void overrideProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
		registry.add("spring.jpa.ddl-auto", () -> "create-drop");
		registry.add("spring.sql.init.mode", () -> "always");
		registry.add("spring.sql.init.schema-locations", () -> "classpath:initial.sql");
		registry.add("spring.jpa.defer-datasource-initialization", () -> true);
		registry.add("jwt.secret-key", () -> "0123456789abcdef0123456789abcdef");
		registry.add("jwt.expiration-ms", () -> 86400000L);
	}

	@Autowired
	private NotificationEventProcessor notificationEventProcessor;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private JsonMapper jsonMapper;

	@Test
	@Sql(scripts = "classpath:prepared-data.sql")
	void processEvent_shouldCreateNotification_whenEventIsValid() {
		Long userId = 7L;
		NotificationType notificationType = NotificationType.NEW_ACHIEVEMENT;
		String eventData = "{\"achievementName\":\"Первые шаги\"}";

		NotificationEvent payload = NotificationEvent.builder()
				.userId(userId)
				.data(eventData)
				.type(notificationType)
				.build();

		PendingEvent pendingEvent = new PendingEvent();
		pendingEvent.setType(EventType.CREATE_NOTIFICATION);
		pendingEvent.setContent(jsonMapper.writeValueAsString(payload));

		// When - Запускаем обработчик события
		notificationEventProcessor.processEvent(pendingEvent);

		// Then - Проверяем, что уведомление реально создалось в БД
		List<NotificationDto> notifications = notificationService.getByUserId(userId)
				.stream()
				.map(n -> new NotificationDto(n.getId(), n.getType(), n.getContent(), n.getCreatedAt(), n.getIsRead()))
				.toList();

		assertThat(notifications).isNotEmpty();

		// Ищем созданное уведомление и проверяем его поля
		NotificationDto createdNotification = notifications.stream()
				.filter(n -> n.getTitle().equals(notificationType.name()))
				.findFirst()
				.orElseThrow();

		assertThat(createdNotification.getContent()).contains("Первые шаги");
		assertThat(createdNotification.getIsRead()).isFalse();
	}
}
