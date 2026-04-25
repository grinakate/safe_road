package ru.itmo.saferoad.services.email;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthEmailService {

	private final JavaMailSender mailSender;

	@Async
	public void sendSimpleMail(@NotNull String to, @NotNull String token) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();

			message.setTo(to);
			message.setFrom("saferoadapp@gmail.com");
			message.setSubject("Confirm your email");
			String body = """
					
					Hello from Road Safe App Team!
					Please use the following link to verify your email:
					
					http://localhost:8080/register/confirmToken?token=%s
					""".formatted(token);
			message.setText(body);
			mailSender.send(message);
		} catch (Exception e) {
			throw new IllegalIdentifierException("Failed to send email");
		}
	}
}
