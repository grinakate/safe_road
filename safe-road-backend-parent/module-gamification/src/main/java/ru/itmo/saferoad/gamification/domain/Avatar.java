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

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"Avatars\"")
@EqualsAndHashCode(of = "id")
public class Avatar {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NonNull
	@Column(nullable = false)
	private String name;

	@NonNull
	@Column(nullable = false, unique = true)
	private String url;

	@NonNull
	@Column(name = "min_level", nullable = false)
	private Integer minLevel;
}
