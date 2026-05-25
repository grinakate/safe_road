package ru.itmo.saferoad.learning.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "learning.adaptive")
public class LearningProperties {

    @NotNull
    private Double reviewRatio = 0.6;

    @NotNull
    private Double newRatio = 0.3;

}
