package ru.itmo.saferoad.content.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.repository.SectionRepository;
import ru.itmo.saferoad.content.dto.SectionTreeDto;

import java.util.List;

@Service
@AllArgsConstructor
public class ContentServiceImpl implements ContentService {

	private final SectionRepository sectionRepository;
	private final ContentMapper contentMapper;

	@NotNull
	@Override
	@Cacheable("courseStructure")
	public List<SectionTreeDto> getSectionTree() {
		List<Section> sections = sectionRepository.findAllWithTopics();
		return contentMapper.toSectionTreeDtoList(sections);
	}
}
