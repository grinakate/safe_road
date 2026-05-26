package ru.itmo.saferoad.gamification.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.api.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.domain.Avatar;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.Level;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

	@Mock
	private GameProfileService gameProfileService;

	@Mock
	private XpHistoryService xpHistoryService;

	@InjectMocks
	private LeaderboardService service;

	@Test
	void buildCurrentUserEntry_usesWeekPeriod_whenPeriodWeek() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(2L);
		when(current.getNickname()).thenReturn("nick");

		Avatar avatar = Instancio.of(Avatar.class)
				.set(field(Avatar::getId), 10)
				.set(field(Avatar::getUrl), "/me.png")
				.set(field(Avatar::getMinLevel), 1)
				.create();

		Level lvl = Instancio.of(Level.class)
				.set(field(Level::getNumber), 1)
				.set(field(Level::getXpThreshold), 100)
				.create();

		GameProfile profile = Instancio.of(GameProfile.class)
				.set(field(GameProfile::getUserId), 2L)
				.set(field(GameProfile::getAvatar), avatar)
				.set(field(GameProfile::getLevel), lvl)
				.set(field(GameProfile::getXp), 20)
				.create();

		when(gameProfileService.existingByUserId(2L)).thenReturn(profile);
		when(xpHistoryService.getWeekXpByUser(2L)).thenReturn(20L);
		when(xpHistoryService.getWeeklyRank(20L)).thenReturn(5L);

		// When
		LeaderboardEntryDto dto = service.buildCurrentUserEntry(current, ru.itmo.saferoad.gamification.domain.LeaderboardPeriod.WEEK);

		// Then
		assertEquals(20L, dto.getXp());
		assertEquals(5L, dto.getRank());
		assertTrue(dto.getIsCurrentUser());
		assertEquals("nick", dto.getNickname());
	}
}



