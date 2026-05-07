package ru.itmo.saferoad.auth.service;

import lombok.NonNull;
import ru.itmo.saferoad.auth.domain.Level;

import java.util.List;

public interface LevelService {

	@NonNull
	Level existingByNumber(@NonNull Integer number);

	@NonNull
	List<Level> getAll();

	@NonNull
	Level findByXp(@NonNull Integer xp);
}
