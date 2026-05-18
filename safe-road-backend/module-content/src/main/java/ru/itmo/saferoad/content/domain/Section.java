package ru.itmo.saferoad.content.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "\"Sections\"")
@EqualsAndHashCode(of = "id")
public class Section {

	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@NonNull
	@Column(name = "title", nullable = false)
	private String title;

	@NonNull
	@Column(name = "order_index", nullable = false, unique = true)
	private Integer orderIndex;

	@NonNull
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@OneToMany(mappedBy = "section", fetch = FetchType.LAZY)
	@OrderBy("orderIndex ASC")
	private List<Topic> topics;
}
