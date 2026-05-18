package ru.itmo.saferoad.content.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.repository.SectionRepository;
import ru.itmo.saferoad.content.service.SectionService;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

	private final SectionRepository sectionRepository;

	@Override
	public @NonNull List<Section> getAllOrdered() {
		return sectionRepository.findAll().stream()
				.sorted(Comparator.comparing(Section::getOrderIndex))
				.toList();
	}

	@Override
	public @NonNull Section create(@NonNull String name, @NonNull Integer orderIndex) {
		Section section = new Section();
		section.setTitle(name);
		section.setOrderIndex(orderIndex);
		return sectionRepository.save(section);
	}

	@Override
	public @NonNull Section existingById(@NonNull Integer id) {
		return sectionRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Section with id " + id + " does not exist"));
	}

	@NonNull
	@Override
	public Section getFirstSection() {
		return sectionRepository.findFirstByOrderByOrderIndexAsc()
				.orElseThrow(() -> new IllegalArgumentException("Section with id " + 1 + " does not exist"));
	}
}
