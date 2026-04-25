package ru.itmo.saferoad.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "levels")
public class Level {

	@Id
	@NonNull
	@Column(name = "id", nullable = false)
	private Long id;

	@NonNull
	@Column(name = "number", nullable = false)
	private Integer number;

	@NonNull
	@Column(name = "title", nullable = false)
	private String title;

	@NonNull
	@Column(name = "xp_threshold", nullable = false)
	private Integer xpThreshold;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Level level)) {
			return false;
		}
		return Objects.equals(getId(), level.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
