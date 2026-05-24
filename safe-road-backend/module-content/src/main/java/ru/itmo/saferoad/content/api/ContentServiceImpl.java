package ru.itmo.saferoad.content.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.repository.SectionRepository;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.content.dto.TopicBriefDto;

import java.util.List;

@Service
@AllArgsConstructor
public class ContentServiceImpl implements ContentService {

	private final SectionRepository sectionRepository;

	@NotNull
	@Override
	@Cacheable("courseStructure")
	public List<SectionTreeDto> getSectionTree() {
		List<Section> sections = sectionRepository.findAllWithTopics();

		return sections.stream().map(s -> SectionTreeDto.builder()
						.id(s.getId())
						.title(s.getTitle())
						.description(s.getDescription())
						.url(s.getUrl())
						.orderIndex(s.getOrderIndex())
						.topics(s.getTopics().stream()
								.map(t -> new TopicBriefDto(
										t.getId(),
										t.getTitle(),
										t.getContent(),
										t.getOrderIndex()))
								.toList()
						).build())
				.toList();
	}
}
