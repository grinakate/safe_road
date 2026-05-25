package ru.itmo.saferoad.gamification.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.api.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.api.mapper.LeaderboardMapperImpl;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardMapperTest {

    @Test
    void toDtoList_assignsRanksAndMarksCurrentUser() {
        // Given
        AppUserDetails current = mock(AppUserDetails.class);
        when(current.getId()).thenReturn(2L);

        LeaderboardProjection p1 = mock(LeaderboardProjection.class);
        when(p1.getUserId()).thenReturn(1L);
        when(p1.getAvatarUrl()).thenReturn("/a.png");
        when(p1.getNickname()).thenReturn("other");
        when(p1.getCurrentXp()).thenReturn(50L);

        LeaderboardProjection p2 = mock(LeaderboardProjection.class);
        when(p2.getUserId()).thenReturn(2L);
        when(p2.getAvatarUrl()).thenReturn("/me.png");
        when(p2.getNickname()).thenReturn("me");
        when(p2.getCurrentXp()).thenReturn(100L);

        var mapper = new LeaderboardMapperImpl();

        // When
        List<LeaderboardEntryDto> result = mapper.toDtoList(List.of(p1, p2), current);

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getRank());
        assertEquals(2L, result.get(1).getRank());
        assertFalse(result.get(0).getIsCurrentUser());
        assertTrue(result.get(1).getIsCurrentUser());
    }
}

