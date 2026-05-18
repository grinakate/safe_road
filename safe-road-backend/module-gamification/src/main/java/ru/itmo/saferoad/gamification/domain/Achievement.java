package ru.itmo.saferoad.gamification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"Achievements\"")
@EqualsAndHashCode(of = "id")
public class Achievement {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NonNull
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@NonNull
	@Column(name = "description", nullable = false)
	private String description;

	@NonNull
	@Column(name = "icon_url", nullable = false)
	private String iconUrl;

	@NonNull
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "requirements", nullable = false)
	private AchievementRequirements requirements;

	@NonNull
	@Column(name = "reward_xp", nullable = false)
	private Integer rewardXp;

	@NonNull
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;
}
