package ru.itmo.saferoad.learning.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSectionResponse {

	@NotNull
	private Integer id;

	@NotNull
	private String title;

	@NotNull
	private Integer progressPercent;

	@NotNull
	private List<TopicUserMapResponse> topics;
}