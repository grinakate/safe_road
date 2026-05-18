package ru.itmo.saferoad.notifications.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "notifications")
public class NotificationsProperties {

    @NotNull
    private Long schedulerPollIntervalMs = 5000L;

    @NotNull
    private Long sseTimeoutMs = 60000L;

    @NotNull
    private Long confirmationTimeoutMs = 60000L;
}

