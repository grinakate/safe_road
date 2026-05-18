package ru.itmo.saferoad.gamification.service;

import jakarta.validation.constraints.NotNull;
import ru.itmo.saferoad.gamification.domain.Avatar;

import java.util.List;

public interface AvatarService {

	@NotNull
	Avatar existingById(@NotNull Integer id);

	@NotNull
	List<Avatar> findAll();
}
