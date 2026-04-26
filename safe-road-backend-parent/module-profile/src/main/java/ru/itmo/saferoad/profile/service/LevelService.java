package ru.itmo.saferoad.profile.service;

import lombok.NonNull;
import ru.itmo.saferoad.profile.domain.Level;

public interface LevelService {

	@NonNull
	Level existingByNumber(@NonNull Integer number);
}
