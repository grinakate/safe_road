package ru.itmo.saferoad.gamification.api.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.api.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaderboardMapperImpl implements LeaderboardMapper {

    @Override
    public List<LeaderboardEntryDto> toDtoList(List<LeaderboardProjection> leaders, AppUserDetails currentUser) {
        List<LeaderboardEntryDto> response = new ArrayList<>(leaders.size());
        for (int i = 0; i < leaders.size(); i++) {
            var h = leaders.get(i);
            var isCurrentUser = h.getUserId().equals(currentUser.getId());
            response.add(new LeaderboardEntryDto(
                    (long) (i + 1),
                    h.getAvatarUrl(),
                    h.getNickname(),
                    h.getCurrentXp(),
                    isCurrentUser
            ));
        }
        return response;
    }
}

