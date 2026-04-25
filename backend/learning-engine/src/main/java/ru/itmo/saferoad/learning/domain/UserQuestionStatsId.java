package ru.itmo.saferoad.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class UserQuestionStatsId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "question_id", nullable = false)
  private Long questionId;
}
