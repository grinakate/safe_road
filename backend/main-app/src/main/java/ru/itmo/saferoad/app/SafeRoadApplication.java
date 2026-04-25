package ru.itmo.saferoad.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {
		"ru.itmo.saferoad.app",
		"ru.itmo.saferoad.services"
})
@EntityScan(basePackages = "ru.itmo.saferoad.domain")
@EnableJpaRepositories(basePackages = "ru.itmo.saferoad.domain.repository")
public class SafeRoadApplication {

	static void main(String[] args) {
		SpringApplication.run(SafeRoadApplication.class, args);
	}

}
