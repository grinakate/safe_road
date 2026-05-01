package ru.itmo.saferoad.content.api;

import jakarta.validation.constraints.NotNull;
import ru.itmo.saferoad.content.dto.SectionTreeDto;

import java.util.List;

public interface ContentService {

	@NotNull
	List<SectionTreeDto> getSectionTree();
}
