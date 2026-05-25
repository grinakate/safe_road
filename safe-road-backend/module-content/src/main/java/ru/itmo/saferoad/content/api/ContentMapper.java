package ru.itmo.saferoad.content.api;

import org.mapstruct.Mapper;
import ru.itmo.saferoad.content.domain.Section;
import ru.itmo.saferoad.content.domain.Topic;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.content.dto.TopicBriefDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContentMapper {

	TopicBriefDto toTopicBriefDto(Topic topic);

	List<TopicBriefDto> toTopicBriefDtoList(List<Topic> topics);

	SectionTreeDto toSectionTreeDto(Section section);

	List<SectionTreeDto> toSectionTreeDtoList(List<Section> sections);
}

