package ru.itmo.saferoad.gamification.domain;

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
@Table(name = "user_metrics")
public class UserMetric {
  @EmbeddedId private UserMetricId id;

  @Column(nullable = false)
  private Long value;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
