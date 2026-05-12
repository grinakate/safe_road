package ru.itmo.saferoad.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.itmo.saferoad.auth.domain.Account;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "notifications")
@EqualsAndHashCode(of = "id")
public class Notification {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private Account user;

	@NonNull
	@Column(name = "title", nullable = false)
	private String title;

	@NonNull
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "content", nullable = false, columnDefinition = "jsonb")
	private Map<String, Object> content;

	@NonNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@NonNull
	@Column(name = "is_read", nullable = false)
	private Boolean isRead;
}

