package ru.itmo.saferoad.gamification.application.scheduler;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "gamification.events")
public class GamificationPendingEventsProperties {

	@NotNull
	private Integer scheduledTreadCount = 10;

	@NotNull
	private Long scheduledPollIntervalMs = 5000L;
}
