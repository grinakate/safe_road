package ru.itmo.saferoad.learning.domain;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "test_sessions")
@EqualsAndHashCode(of = "id")
public class TestSession {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NonNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "topic_id")
	private Integer topicId;

	@Column(name = "section_id")
	private Integer sectionId;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "mode", nullable = false)
	private TestMode mode;

	@NonNull
	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "status", nullable = false, columnDefinition = "progress_status")
	private ProgressStatus status;

	@NonNull
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "questions_data", nullable = false, columnDefinition = "jsonb")
	private Map<String, Object> questionsData;

	@NonNull
	@Column(name = "total_questions", nullable = false)
	private Integer totalQuestions;

	@NonNull
	@Column(name = "correct_count", nullable = false)
	private Integer correctCount;

	@NonNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "finished_at", nullable = true)
	private LocalDateTime finishedAt;
}

