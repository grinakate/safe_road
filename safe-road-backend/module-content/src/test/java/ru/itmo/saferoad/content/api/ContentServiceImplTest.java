package ru.itmo.saferoad.content.api;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.repository.SectionRepository;
import ru.itmo.saferoad.content.dto.SectionTreeDto;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceImplTest {

	@Mock
	private SectionRepository sectionRepository;

	@Mock
	private ContentMapper contentMapper;

	@InjectMocks
	private ContentServiceImpl contentService;

	@Test
	void getSectionTree_shouldReturnMappedList() {
		// Given
		Section s = Instancio.of(Section.class).set(field(Section::getTopics), List.of()).create();
		when(sectionRepository.findAllWithTopics()).thenReturn(List.of(s));
		SectionTreeDto dto = Instancio.create(SectionTreeDto.class);
		when(contentMapper.toSectionTreeDtoList(List.of(s))).thenReturn(List.of(dto));

		// When
		var res = contentService.getSectionTree();

		// Then
		assertEquals(1, res.size());
		assertSame(dto, res.get(0));
		verify(sectionRepository, times(1)).findAllWithTopics();
		verify(contentMapper, times(1)).toSectionTreeDtoList(List.of(s));
	}
}

