package ru.itmo.saferoad.profile.mapper;

import org.mapstruct.Mapper;
import ru.itmo.saferoad.core.dto.profile.level.LevelResponse;
import ru.itmo.saferoad.profile.domain.Level;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LevelMapper {

	LevelResponse mapToResponse(Level level);

	List<LevelResponse> mapToResponse(List<Level> level);
}
