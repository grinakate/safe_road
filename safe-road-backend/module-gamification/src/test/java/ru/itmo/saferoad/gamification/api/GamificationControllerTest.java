package ru.itmo.saferoad.gamification.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.itmo.saferoad.auth.application.UserService;
import ru.itmo.saferoad.core.security.AppUserDetails;
import ru.itmo.saferoad.gamification.api.dto.LeaderboardEntryDto;
import ru.itmo.saferoad.gamification.api.mapper.LeaderboardMapper;
import ru.itmo.saferoad.gamification.domain.Avatar;
import ru.itmo.saferoad.gamification.domain.GameProfile;
import ru.itmo.saferoad.gamification.domain.LeaderboardPeriod;
import ru.itmo.saferoad.gamification.domain.Level;
import ru.itmo.saferoad.gamification.domain.XpHistory;
import ru.itmo.saferoad.gamification.domain.repository.LeaderboardProjection;
import ru.itmo.saferoad.gamification.service.AchievementService;
import ru.itmo.saferoad.gamification.service.AvatarService;
import ru.itmo.saferoad.gamification.service.GameProfileService;
import ru.itmo.saferoad.gamification.service.LeaderboardService;
import ru.itmo.saferoad.gamification.service.LevelService;
import ru.itmo.saferoad.gamification.service.XpHistoryService;
import org.instancio.Instancio;

import static org.instancio.Select.field;

import ru.itmo.saferoad.gamification.api.dto.AvatarDto;
import ru.itmo.saferoad.gamification.api.dto.ChangeAvatarRequest;
import ru.itmo.saferoad.gamification.api.dto.UpdateProfileRequest;
import ru.itmo.saferoad.gamification.api.dto.XpHistoryDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationControllerTest {

	@Mock
	private UserService userService;

	@Mock
	private LevelService levelService;

	@Mock
	private AvatarService avatarService;

	@Mock
	private XpHistoryService xpHistoryService;

	@Mock
	private GameProfileService gameProfileService;

	@Mock
	private AchievementService achievementService;

	@Mock
	private LeaderboardMapper leaderboardMapper;

	@Mock
	private LeaderboardService leaderboardService;

	@InjectMocks
	private GamificationController controller;

	@Test
	void getLeaderboard_whenCurrentInTop_returnsListWithCurrentMarked() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		List<LeaderboardProjection> leaders = new ArrayList<>();
		leaders.add(mock(LeaderboardProjection.class));
		var period = LeaderboardPeriod.WEEK;


		when(xpHistoryService.getTop10ForCurrentWeek()).thenReturn(leaders);

		LeaderboardEntryDto dto = Instancio.of(LeaderboardEntryDto.class)
				.set(field(LeaderboardEntryDto::getIsCurrentUser), true)
				.create();
		List<LeaderboardEntryDto> dtoList = new ArrayList<>();
		dtoList.add(dto);

		when(leaderboardMapper.toDtoList(leaders, current)).thenReturn(dtoList);

		// When
		ResponseEntity<List<LeaderboardEntryDto>> resp = controller.getLeaderboard(current, period.getValue());

		// Then
		assertEquals(1, resp.getBody().size());
		assertTrue(resp.getBody().contains(dto));
	}

	@Test
	void getLeaderboard_whenCurrentNotInTop_appendsCurrentUser() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		List<LeaderboardProjection> leaders = new ArrayList<>();
		leaders.add(mock(LeaderboardProjection.class));
		var period = LeaderboardPeriod.WEEK;


		when(xpHistoryService.getTop10ForCurrentWeek()).thenReturn(leaders);

		LeaderboardEntryDto dto = Instancio.of(LeaderboardEntryDto.class)
				.set(field(LeaderboardEntryDto::getIsCurrentUser), false)
				.create();
		List<LeaderboardEntryDto> dtoList = new ArrayList<>();
		dtoList.add(dto);

		when(leaderboardMapper.toDtoList(leaders, current)).thenReturn(dtoList);

		LeaderboardEntryDto currentUserDto = Instancio.create(LeaderboardEntryDto.class);
		when(leaderboardService.buildCurrentUserEntry(current, period)).thenReturn(currentUserDto);

		// When
		ResponseEntity<List<LeaderboardEntryDto>> resp = controller.getLeaderboard(current, period.getValue());

		// Then
		assertEquals(2, resp.getBody().size());
		assertTrue(resp.getBody().contains(dto));
		assertTrue(resp.getBody().contains(currentUserDto));
	}

	@Test
	void getMe_returnsProfileMapped() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(7L);
		when(current.getNickname()).thenReturn("nick");

		var avatar = Instancio.of(Avatar.class)
				.set(field(Avatar::getId), 11)
				.set(field(Avatar::getUrl), "/a.png")
				.set(field(Avatar::getMinLevel), 1)
				.create();

		var lvl = Instancio.of(Level.class)
				.set(field(Level::getNumber), 3)
				.create();

		var profile = Instancio.of(GameProfile.class)
				.set(field(GameProfile::getUserId), 7L)
				.set(field(GameProfile::getAvatar), avatar)
				.set(field(GameProfile::getLevel), lvl)
				.set(field(GameProfile::getXp), 42)
				.set(field(GameProfile::getCurrentStreak), 2)
				.set(field(GameProfile::getIsLeaderboardParticipant), true)
				.create();

		when(gameProfileService.findById(7L)).thenReturn(java.util.Optional.of(profile));
		when(levelService.getXpProgress(3, 42)).thenReturn(0.5);

		// When
		ResponseEntity<?> resp = controller.getMe(current);

		// Then
		assertTrue(resp.getStatusCode().is2xxSuccessful());
		var body = (ru.itmo.saferoad.gamification.api.dto.UserGameProfileDto) resp.getBody();
		assertEquals("nick", body.getName());
		assertEquals(3, body.getLevel());
		assertEquals(42, body.getCurrentXp());
		assertEquals(0.5, body.getXpProgress());
		assertEquals(11, body.getAvatar().getId());
		assertTrue(body.getLeaderboardEnabled());
	}

	@Test
	void changeMyAvatar_returnsBadRequest_whenLevelTooLow() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(5L);

		var newAvatar = Instancio.of(Avatar.class)
				.set(field(Avatar::getId), 99)
				.set(field(Avatar::getMinLevel), 10)
				.create();

		var lvl = Instancio.of(Level.class).set(field(Level::getNumber), 1).create();
		var profile = Instancio.of(GameProfile.class)
				.set(field(GameProfile::getUserId), 5L)
				.set(field(GameProfile::getLevel), lvl)
				.create();

		when(avatarService.existingById(99)).thenReturn(newAvatar);
		when(gameProfileService.existingByUserId(5L)).thenReturn(profile);

		ChangeAvatarRequest req = Instancio.of(ChangeAvatarRequest.class)
				.set(field(ChangeAvatarRequest::getAvatarId), 99)
				.create();

		// When
		ResponseEntity<AvatarDto> resp = controller.changeMyAvatar(current, req);

		// Then
		assertEquals(400, resp.getStatusCode().value());
	}

	@Test
	void changeMyAvatar_success() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(6L);

		var newAvatar = Instancio.of(Avatar.class)
				.set(field(Avatar::getId), 12)
				.set(field(Avatar::getUrl), "/ok.png")
				.set(field(Avatar::getMinLevel), 1)
				.create();

		var lvl = Instancio.of(Level.class).set(field(Level::getNumber), 5).create();
		var profile = Instancio.of(GameProfile.class)
				.set(field(GameProfile::getUserId), 6L)
				.set(field(GameProfile::getLevel), lvl)
				.create();

		when(avatarService.existingById(12)).thenReturn(newAvatar);
		when(gameProfileService.existingByUserId(6L)).thenReturn(profile);

		ChangeAvatarRequest req = Instancio.of(ChangeAvatarRequest.class)
				.set(field(ChangeAvatarRequest::getAvatarId), 12)
				.create();

		// When
		ResponseEntity<AvatarDto> resp = controller.changeMyAvatar(current, req);

		// Then
		assertTrue(resp.getStatusCode().is2xxSuccessful());
		AvatarDto body = resp.getBody();
		assertEquals(12, body.getId());
		assertEquals("/ok.png", body.getUrl());
		assertTrue(body.getIsAvailable());
	}

	@Test
	void getMyAchievements_returnsList() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(8L);

		var ach = Instancio.create(ru.itmo.saferoad.gamification.api.dto.UserAchievementsResponse.class);
		when(achievementService.getAchievementsForUser(8L)).thenReturn(List.of(ach));

		// When
		ResponseEntity<List<ru.itmo.saferoad.gamification.api.dto.UserAchievementsResponse>> resp = controller.getMyAchievements(current);

		// Then
		assertEquals(1, resp.getBody().size());
	}

	@Test
	void getAvatars_marksAvailability() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(9L);

		var lvl = Instancio.of(Level.class).set(field(Level::getNumber), 2).create();
		var profile = Instancio.of(GameProfile.class)
				.set(field(GameProfile::getUserId), 9L)
				.set(field(GameProfile::getLevel), lvl)
				.create();

		var a1 = Instancio.of(Avatar.class).set(field(Avatar::getMinLevel), 1).create();
		var a2 = Instancio.of(Avatar.class).set(field(Avatar::getMinLevel), 10).create();

		when(gameProfileService.existingByUserId(9L)).thenReturn(profile);
		when(avatarService.findAll()).thenReturn(List.of(a1, a2));

		// When
		ResponseEntity<List<AvatarDto>> resp = controller.getAvatars(current);

		// Then
		List<AvatarDto> list = resp.getBody();
		assertEquals(2, list.size());
		assertTrue(list.get(0).getIsAvailable());
		assertFalse(list.get(1).getIsAvailable());
	}

	@Test
	void toggleLeaderboard_togglesFlag() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(10L);

		var profile = Instancio.of(GameProfile.class)
				.set(field(GameProfile::getUserId), 10L)
				.set(field(GameProfile::getIsLeaderboardParticipant), false)
				.create();

		when(gameProfileService.existingByUserId(10L)).thenReturn(profile);

		// When
		ResponseEntity<Void> resp = controller.toggleLeaderboard(current);

		// Then
		assertTrue(resp.getStatusCode().is2xxSuccessful());
		assertTrue(profile.getIsLeaderboardParticipant());
	}

	@Test
	void getHistory_mapsXpHistory() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(12L);

		XpHistory h = Instancio.of(XpHistory.class)
				.set(field(XpHistory::getUserId), 12L)
				.set(field(XpHistory::getXp), 123L)
				.create();

		when(xpHistoryService.findAllByUserId(12L)).thenReturn(List.of(h));

		// When
		ResponseEntity<List<XpHistoryDto>> resp = controller.getHistory(current);

		// Then
		List<XpHistoryDto> list = resp.getBody();
		assertEquals(1, list.size());
		assertEquals(123L, list.get(0).getXp());
	}

	@Test
	void updateProfileSettings_callsUserServiceAndGameProfile() {
		// Given
		AppUserDetails current = mock(AppUserDetails.class);
		when(current.getId()).thenReturn(13L);

		UpdateProfileRequest req = Instancio.of(UpdateProfileRequest.class)
				.set(field(UpdateProfileRequest::getNickname), "newnick")
				.set(field(UpdateProfileRequest::getPassword), "newpass123")
				.set(field(UpdateProfileRequest::getBirthDate), java.time.LocalDate.of(1990, 1, 1))
				.set(field(UpdateProfileRequest::getLeaderboardEnabled), true)
				.create();

		// When
		ResponseEntity<?> resp = controller.updateProfileSettings(current, req);

		// Then
		assertTrue(resp.getStatusCode().is2xxSuccessful());
		verify(userService, times(1)).update(eq(13L), eq("newnick"), eq("newpass123"), eq(java.time.LocalDate.of(1990, 1, 1)));
		verify(gameProfileService, times(1)).setLeaderboardEnabled(13L, true);
	}
}

