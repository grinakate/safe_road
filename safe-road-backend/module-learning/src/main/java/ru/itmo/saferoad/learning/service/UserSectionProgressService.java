package ru.itmo.saferoad.learning.service;

import lombok.NonNull;
import ru.itmo.saferoad.core.domain.enums.ProgressStatus;
import ru.itmo.saferoad.learning.domain.UserSectionProgress;

import java.util.List;

public interface UserSectionProgressService {

	@NonNull
	List<UserSectionProgress> getByUserId(@NonNull Long userId);

	@NonNull
	UserSectionProgress upsertStatus(@NonNull Long userId,
	                                 @NonNull Integer sectionId,
	                                 @NonNull ProgressStatus status);
}
