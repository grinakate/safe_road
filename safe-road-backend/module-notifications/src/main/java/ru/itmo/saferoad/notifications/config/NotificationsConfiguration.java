package ru.itmo.saferoad.notifications.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class NotificationsConfiguration {

    @Bean
    public ExecutorService notificationsExecutor(NotificationsProperties properties) {
        return Executors.newFixedThreadPool(properties.getSchedulerSendingThreadsCount());
    }
}

