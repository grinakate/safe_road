package ru.itmo.saferoad.gamification.domain;

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
@IdClass(UserAchievementId.class)
@Table(name = "user_achievements")
@EqualsAndHashCode(of = {"userId", "achievementId"})
public class UserAchievement {

	@Id
	@NonNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Id
	@NonNull
	@Column(name = "achievement_id", nullable = false)
	private Integer achievementId;

	@NonNull
	@Column(name = "earned_at", nullable = false)
	private LocalDateTime earnedAt;
}
