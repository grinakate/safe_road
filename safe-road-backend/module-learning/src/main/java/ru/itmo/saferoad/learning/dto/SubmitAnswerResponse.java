package ru.itmo.saferoad.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerResponse {
	private boolean correct;
	private int awardedXp;
}

