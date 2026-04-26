package ru.itmo.saferoad.learning.config;


import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "ru.itmo.saferoad.learning.domain")
@EnableJpaRepositories(basePackages = "ru.itmo.saferoad.learning.domain.repository")
public class LearningDomainConfig {
}
