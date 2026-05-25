package ru.itmo.saferoad.learning.service.impl;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserSectionProgress;
import ru.itmo.saferoad.learning.service.UserSectionProgressService;

import java.util.List;

@Service
public class UserSectionProgressServiceImpl implements UserSectionProgressService {
	@Override
	public @NonNull List<UserSectionProgress> getByUserId(@NonNull Long userId) {
		return List.of();
	}

	@Override
	public @NonNull UserSectionProgress upsertStatus(@NonNull Long userId, @NonNull Integer sectionId, @NonNull ProgressStatus status) {
		return null;
	}
}
