package ru.itmo.saferoad.gamification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class UserMetricId implements Serializable {
  private static final long serialVersionUID = 1L;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "metric_code", nullable = false)
  private String metricCode;
}
