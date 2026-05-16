package ru.itmo.saferoad.content.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"Topics\"")
@EqualsAndHashCode(of = "id")
public class Topic {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "section_id", nullable = false)
	private Section section;

	@NonNull
	@Column(name = "name", nullable = false)
	private String name;

	@NonNull
	@Column(name = "description")
	private String description;

	@NonNull
	@Column(name = "order_index", nullable = false, unique = true)
	private Integer orderIndex;

	@NonNull
	@Column(name = "xp_reward", nullable = false)
	private Integer xpReward;
}
