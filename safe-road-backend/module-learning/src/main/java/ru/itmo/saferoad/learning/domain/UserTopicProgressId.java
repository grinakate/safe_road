package ru.itmo.saferoad.learning.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTopicProgressId implements Serializable {

	@NonNull
	private Long userId;

	@NonNull
	private Integer topicId;
}
