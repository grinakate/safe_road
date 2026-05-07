package ru.itmo.saferoad.auth.service;

import lombok.NonNull;
import ru.itmo.saferoad.auth.domain.Avatar;

import java.util.List;

public interface AvatarService {

	@NonNull
	Avatar existingById(@NonNull Integer id);

	@NonNull
	Avatar save(@NonNull Avatar avatar);

	@NonNull
	List<Avatar> getAll();
}
