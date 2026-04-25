package ru.itmo.saferoad.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_question_stats")
public class UserQuestionStats {
  @EmbeddedId private UserQuestionStatsId id;

  @Column(name = "topic_id", nullable = false)
  private Integer topicId;

  @Column(name = "success_streak", nullable = false)
  private Integer successStreak;

  @Column(name = "last_result_correct", nullable = false)
  private Boolean lastResultCorrect;

  @Column(name = "next_review_at", nullable = false)
  private LocalDateTime nextReviewAt;
}
