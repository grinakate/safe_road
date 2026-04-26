package ru.itmo.saferoad.profile.config;


import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "ru.itmo.saferoad.profile.domain")
@EnableJpaRepositories(basePackages = "ru.itmo.saferoad.profile.domain.repository")
public class ProfileDomainConfig {
}
