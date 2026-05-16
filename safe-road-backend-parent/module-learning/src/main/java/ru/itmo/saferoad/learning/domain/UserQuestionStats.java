package ru.itmo.saferoad.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(UserQuestionStatsId.class)
@Table(name = "\"UserQuestionStats\"")
@EqualsAndHashCode(of = {"userId", "questionId"})
public class UserQuestionStats {

	@Id
	@NonNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Id
	@NonNull
	@Column(name = "question_id", nullable = false)
	private Long questionId;

	@NonNull
	@Column(name = "topic_id", nullable = false)
	private Integer topicId;

	@NonNull
	@Column(name = "success_streak", nullable = false)
	private Integer successStreak;

	@NonNull
	@Column(name = "last_result_correct", nullable = false)
	private Boolean lastResultCorrect;

	@NonNull
	@Column(name = "next_review_at", nullable = false)
	private LocalDateTime nextReviewAt;
}
