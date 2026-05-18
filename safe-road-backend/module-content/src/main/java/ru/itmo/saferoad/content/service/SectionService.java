package ru.itmo.saferoad.content.service;

import lombok.NonNull;
import ru.itmo.saferoad.content.domain.Section;

import java.util.List;

public interface SectionService {

	@NonNull
	List<Section> getAllOrdered();

	@NonNull
	Section create(@NonNull String name, @NonNull Integer orderIndex);

	@NonNull
	Section existingById(@NonNull Integer id);

	@NonNull
	Section getFirstSection();
}
