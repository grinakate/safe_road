package ru.itmo.saferoad.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableCaching
@EnableScheduling
@EnableWebSecurity
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {
		"ru.itmo.saferoad.app",
		"ru.itmo.saferoad.core",
		"ru.itmo.saferoad.content",
		"ru.itmo.saferoad.learning",
		"ru.itmo.saferoad.auth",
		"ru.itmo.saferoad.gamification"
})
public class SafeRoadApplication {

	static void main(String[] args) {
		SpringApplication.run(SafeRoadApplication.class, args);
	}

}
