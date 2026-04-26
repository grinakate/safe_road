package ru.itmo.saferoad.gamification.config;


import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "ru.itmo.saferoad.gamification.domain")
@EnableJpaRepositories(basePackages = "ru.itmo.saferoad.gamification.domain.repository")
public class GamificationDomainConfig {
}
