package ru.itmo.saferoad.gamification.domain;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.itmo.saferoad.learning.domain.ProgressStatus;

import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "pending_events")
@EqualsAndHashCode(of = "id")
public class PendingEvent {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private EventType type;

	@NonNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, columnDefinition = "progress_status")
	private ProgressStatus status;

	@NonNull
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "content", nullable = false, columnDefinition = "jsonb")
	private Map<String, Object> content;
}

