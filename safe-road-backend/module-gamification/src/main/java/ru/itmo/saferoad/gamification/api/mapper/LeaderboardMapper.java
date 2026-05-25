package ru.itmo.saferoad.gamification.api.mapper;

import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.api.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;

import java.util.List;

public interface LeaderboardMapper {

	List<LeaderboardEntryDto> toDtoList(List<LeaderboardProjection> leaders, AppUserDetails currentUser);

}

