package ru.itmo.saferoad.auth.infrastructure.config;


import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "ru.itmo.saferoad.profile.domain")
@EnableJpaRepositories(basePackages = "ru.itmo.saferoad.auth.domain.repository")
public class AuthDomainConfig {
}
