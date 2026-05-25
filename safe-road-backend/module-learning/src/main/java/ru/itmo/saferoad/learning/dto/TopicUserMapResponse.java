package ru.itmo.saferoad.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicUserMapResponse {

	@NotNull
	private Integer id;

	@NotNull
	private String title;

	@NotNull
	private Integer orderIndex;

	@NotNull
	private ProgressStatus status;
}
