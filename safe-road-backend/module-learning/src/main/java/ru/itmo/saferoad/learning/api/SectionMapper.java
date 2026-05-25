package ru.itmo.saferoad.learning.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.saferoad.content.dto.SectionTreeDto;
import ru.itmo.saferoad.content.dto.TopicBriefDto;
import ru.itmo.saferoad.learning.dto.TopicUserMapResponse;
import ru.itmo.saferoad.learning.dto.UserSectionResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    @Mapping(target = "progressPercent", constant = "0")
    @Mapping(target = "topics", source = "topics")
    UserSectionResponse toUserSectionResponse(SectionTreeDto section);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "orderIndex", source = "orderIndex")
    @Mapping(target = "status", expression = "java(ru.itmo.saferoad.core.domain.enums.ProgressStatus.LOCKED)")
    TopicUserMapResponse toTopicUserMapResponse(TopicBriefDto topic);

    List<UserSectionResponse> toUserSectionResponseList(List<SectionTreeDto> sections);
}

