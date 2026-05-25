package ru.itmo.saferoad.learning.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.learning.domain.ReviewInterval;
import ru.itmo.saferoad.learning.domain.repository.ReviewIntervalRepository;
import ru.itmo.saferoad.learning.service.ReviewIntervalService;

@Service
@RequiredArgsConstructor
public class ReviewIntervalServiceImpl implements ReviewIntervalService {

	private final ReviewIntervalRepository reviewIntervalRepository;

	@Override
	public @NonNull ReviewInterval existingByStreakLevel(@NonNull Integer streakLevel) {
		return reviewIntervalRepository.findById(streakLevel)
				.orElseThrow(() -> new IllegalArgumentException("Review interval for streak "
						+ streakLevel + " does not exist"));
	}
}
