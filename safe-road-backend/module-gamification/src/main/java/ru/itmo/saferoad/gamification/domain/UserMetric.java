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
@IdClass(UserMetricId.class)
@Table(name = "\"UserMetrics\"")
@EqualsAndHashCode(of = {"userId", "metricCode"})
public class UserMetric {

	@Id
	@NonNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Id
	@NonNull
	@Column(name = "metric_code", nullable = false)
	private String metricCode;

	@NonNull
	@Column(name = "value", nullable = false)
	private Long value;

	@NonNull
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
