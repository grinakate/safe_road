package ru.itmo.saferoad.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.itmo.saferoad.core.types.ProgressStatus;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(UserSectionProgressId.class)
@Table(name = "\"UserSectionProgress\"")
@EqualsAndHashCode(of = {"userId", "sectionId"})
public class UserSectionProgress {

	@Id
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Id
	@Column(name = "section_id", nullable = false)
	private Integer sectionId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "progress_status")
	private ProgressStatus status;
}
