package ru.itmo.saferoad.core.domain;

import io.hypersistence.utils.hibernate.type.json.JsonType;
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
import org.hibernate.annotations.Type;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import ru.itmo.saferoad.core.domain.enums.EventType;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;

import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"PendingEvents\"")
@EqualsAndHashCode(of = "id")
public class PendingEvents {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(nullable = false)
	private EventType type;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(nullable = false)
	private ProgressStatus status;

	@Type(JsonType.class)
	@Column(columnDefinition = "jsonb", nullable = false)
	private Map<String, Object> content;
}
