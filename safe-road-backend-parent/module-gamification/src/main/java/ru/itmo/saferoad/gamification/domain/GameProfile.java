package ru.itmo.saferoad.gamification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "\"GameProfiles\"")
@EqualsAndHashCode(of = "userId")
public class GameProfile {

	@Id
	@NonNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "level_id", nullable = false)
	private Level level;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "avatar_id", nullable = false)
	private Avatar avatar;

	@NonNull
	@Column(name = "current_xp", nullable = false)
	private Integer currentXp;

	@NonNull
	@Column(name = "current_streak", nullable = false)
	private Integer currentStreak;

	@Column(name = "last_activity_date", nullable = true)
	private LocalDateTime lastActivityDate;

	@NonNull
	@Column(name = "total_active_days", nullable = false)
	private Integer totalActiveDays;

	@NonNull
	@Column(name = "is_leaderboard_participant", nullable = false)
	private Boolean isLeaderboardParticipant;
}

