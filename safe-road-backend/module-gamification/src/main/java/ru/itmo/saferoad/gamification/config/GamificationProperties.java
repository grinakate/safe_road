package ru.itmo.saferoad.gamification.config;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "gamification.config")
public class GamificationProperties {

	@Positive
	@NotNull
	private Integer minAgeForDefaultLeaderBoarder = 12;

	@Positive
	@NotNull
	private Integer baseXpPerQuestion = 10;

	@NotNull
	private Map<String, Double> questionTypeCoefficients = new HashMap<>();
}
