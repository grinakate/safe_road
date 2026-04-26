package ru.itmo.saferoad.profile.service;

import lombok.NonNull;
import ru.itmo.saferoad.profile.domain.Avatar;

public interface AvatarService {

	@NonNull
	Avatar existingById(@NonNull Integer id);
}
