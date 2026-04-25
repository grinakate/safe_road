package ru.itmo.saferoad.profile.domain;

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
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.saferoad.profile.domain.enums.UserRole;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(nullable = false)
  private String name;

  @Column(name = "birth_date", nullable = false)
  private LocalDateTime birthDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "user_role")
  private UserRole role;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "level_id", nullable = false)
  private Level level;

  @Column(name = "current_xp", nullable = false)
  private Integer currentXp;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "avatar_id", nullable = false)
  private Avatar avatar;

  @Column(name = "last_login_date", nullable = false)
  private LocalDateTime lastLoginDate;

  @Column(name = "current_streak", nullable = false)
  private Integer currentStreak;
}
