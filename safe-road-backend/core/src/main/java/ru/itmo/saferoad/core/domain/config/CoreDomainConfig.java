package ru.itmo.saferoad.core.domain.config;


import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "ru.itmo.saferoad.core.domain")
@EnableJpaRepositories(basePackages = "ru.itmo.saferoad.core.domain.repository")
public class CoreDomainConfig {
}
