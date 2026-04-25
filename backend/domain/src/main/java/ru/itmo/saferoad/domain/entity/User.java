package ru.itmo.saferoad.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.itmo.saferoad.domain.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Информация о пользователе.
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails {

	@Id
	@NonNull
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
	@SequenceGenerator(sequenceName = "users_seq", name = "user_sequence", allocationSize = 1)
	private Long id;

	@NonNull
	@Column(name = "email", nullable = false)
	private String email;

	@NonNull
	@Column(name = "name", nullable = false)
	private String name;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "role", nullable = false)
	private UserRole role;

	@NonNull
	@Column(name = "password_hash", nullable = false)
	private String password;

	@NonNull
	@Column(name = "email_verified", nullable = false)
	private Boolean emailVerified;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "avatar_id", nullable = false)
	private Avatar avatar;

	@NonNull
	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;

	@NonNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NonNull
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@NonNull
	@Column(name = "current_xp", nullable = false)
	private Long currentXp;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "level_id", nullable = false)
	private Level level;

	@NonNull
	@Column(name = "coins", nullable = false)
	private Long coins;

	@NonNull
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@NonNull
	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof User user)) {
			return false;
		}
		return Objects.equals(getId(), user.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
