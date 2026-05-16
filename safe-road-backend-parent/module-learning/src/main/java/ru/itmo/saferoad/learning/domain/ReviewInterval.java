package ru.itmo.saferoad.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = "streakLevel")
@Table(name = "\"ReviewIntervals\"")
public class ReviewInterval {

	@Id
	@NonNull
	@Column(name = "streak_level", nullable = false)
	private Integer streakLevel;

	@NonNull
	@Column(name = "interval_hours", nullable = false)
	private Integer intervalHours;
}
