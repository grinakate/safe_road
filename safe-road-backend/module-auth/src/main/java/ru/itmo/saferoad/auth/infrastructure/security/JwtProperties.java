package ru.itmo.saferoad.auth.infrastructure.security;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	@NotNull
	@Size(min = 32)
	private String secretKey;

	@NotNull
	@Positive
	private Long expirationMs;

}
