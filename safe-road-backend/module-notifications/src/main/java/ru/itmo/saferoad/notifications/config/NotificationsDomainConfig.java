package ru.itmo.saferoad.notifications.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "ru.itmo.saferoad.notifications.domain")
@EnableJpaRepositories(basePackages = "ru.itmo.saferoad.notifications.domain.repository")
public class NotificationsDomainConfig {
}

