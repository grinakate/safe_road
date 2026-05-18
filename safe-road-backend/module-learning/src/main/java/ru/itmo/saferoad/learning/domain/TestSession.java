package ru.itmo.saferoad.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.Topic;
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
	private Integer id;

	@NonNull
	@Column(name = "user_id", nullable = false)
	private Long user_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_id")
	private Topic topic;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id", nullable = true)
	private Section section;

	@NonNull
	@Enumerated(EnumType.STRING)
	@Column(name = "mode", nullable = false)
	private TestMode mode;

	@NonNull
	@Enumerated(EnumType.STRING)
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

