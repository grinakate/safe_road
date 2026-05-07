package ru.itmo.saferoad.auth.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.auth.domain.Account;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@AllArgsConstructor
public class JwtUtils {

	private final JwtProperties jwtProperties;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(Account user) {
		return Jwts.builder()
				.subject(user.getEmail())
				.claim("email", user.getEmail())
				.claim("role", user.getRole())
				.issuedAt(new Date())
				.expiration(new Date((new Date()).getTime() + jwtProperties.getExpirationMs()))
				.signWith(getSigningKey(), Jwts.SIG.HS256)
				.compact();
	}

	public String getEmailFromToken(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build()
				.parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

