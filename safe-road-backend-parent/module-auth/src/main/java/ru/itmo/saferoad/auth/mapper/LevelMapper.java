package ru.itmo.saferoad.auth.mapper;

import org.mapstruct.Mapper;
import ru.itmo.saferoad.auth.dto.level.LevelResponse;
import ru.itmo.saferoad.auth.domain.Level;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LevelMapper {

	LevelResponse mapToResponse(Level level);

	List<LevelResponse> mapToResponse(List<Level> level);
}
