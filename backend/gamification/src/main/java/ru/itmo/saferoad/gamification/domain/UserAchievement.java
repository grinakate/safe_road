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
@Table(name = "user_achievements")
public class UserAchievement {
  @EmbeddedId private UserAchievementId id;

  @Column(name = "earned_at", nullable = false)
  private LocalDateTime earnedAt;
}
