package ru.itmo.saferoad.notifications.application.scheduler;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "notifications.events")
public class NotificationsPendingEventsProperties {

    @NotNull
    private Integer scheduledTreadCount = 5;
}

