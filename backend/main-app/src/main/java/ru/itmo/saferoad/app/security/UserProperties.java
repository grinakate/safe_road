package ru.itmo.saferoad.app.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("spring.security.user")
public class UserProperties {

	private String name;

	private String password;

}
