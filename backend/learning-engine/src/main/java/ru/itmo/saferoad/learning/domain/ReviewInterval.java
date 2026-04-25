package ru.itmo.saferoad.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "review_intervals")
public class ReviewInterval {
  @Id
  @Column(name = "streak_level", nullable = false)
  private Integer streakLevel;

  @Column(name = "interval_hours", nullable = false)
  private Integer intervalHours;
}
