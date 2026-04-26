package ru.itmo.saferoad.profile.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "safe-road.new.user")
public class NewUserProperties {

	private Integer startLevelNumber = 0;

	private Integer startXp = 0;

	private Integer startStreak = 1;
}
