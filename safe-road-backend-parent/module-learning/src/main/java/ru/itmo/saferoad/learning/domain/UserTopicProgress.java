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
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(UserTopicProgressId.class)
@Table(name = "user_topic_progress")
@EqualsAndHashCode(of = {"userId", "topicId"})
public class UserTopicProgress {

	@Id
	@NonNull
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Id
	@NonNull
	@Column(name = "topic_id", nullable = false)
	private Integer topicId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "progress_status")
	private ProgressStatus status;
}
