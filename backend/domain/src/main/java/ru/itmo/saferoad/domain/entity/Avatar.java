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
@Table(name = "avatars")
public class Avatar {

	@Id
	@NonNull
	@Column(name = "id", nullable = false)
	private Long id;

	@NonNull
	@Column(name = "name", nullable = false)
	private String name;

	@NonNull
	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Avatar avatar)) {
			return false;
		}
		return Objects.equals(getId(), avatar.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}
