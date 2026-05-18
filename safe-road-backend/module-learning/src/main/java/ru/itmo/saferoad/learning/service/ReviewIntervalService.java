package ru.itmo.saferoad.learning.service;

import lombok.NonNull;
import ru.itmo.saferoad.learning.domain.ReviewInterval;

public interface ReviewIntervalService {

	@NonNull
	ReviewInterval existingByStreakLevel(@NonNull Integer streakLevel);
}
