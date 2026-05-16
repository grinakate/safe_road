package ru.itmo.saferoad.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"Users\"")
@EqualsAndHashCode(of = "id")
public class Users {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@Column(nullable = false, unique = true)
	private String email;

	@NonNull
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@NonNull
	@Column(name = "nickname", nullable = false)
	private String nickname;

	@NonNull
	@Column(name = "birth_date", nullable = false)
	private LocalDateTime birthDate;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(nullable = false)
	private UserRole role;

	@NonNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NonNull
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@NonNull
	@Column(name = "last_login_date", nullable = false)
	private LocalDateTime lastLoginDate;
}
